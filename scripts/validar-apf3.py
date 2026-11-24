#!/usr/bin/env python3
"""Validación funcional y carga limitada exclusivamente al despliegue local APF3."""
from pathlib import Path
import json,time,urllib.request,urllib.error,concurrent.futures,threading,secrets,math,subprocess,statistics,platform,datetime,os
R=Path(__file__).resolve().parents[1];OUT=R/'docs/apf3/evidencias';OUT.mkdir(parents=True,exist_ok=True)
BASE='http://127.0.0.1:8083';env=dict(l.split('=',1) for l in (R/'.env').read_text().splitlines() if '=' in l and not l.startswith('#'))
cases=[];token='';suffix=secrets.token_hex(4)
def mask(x):
 if isinstance(x,dict):return {k:('[REDACTADO]' if any(s in k.lower() for s in ['token','password','secret']) else mask(v)) for k,v in x.items()}
 if isinstance(x,list):return [mask(a) for a in x]
 return x
def request(method,path,body=None,bearer=None):
 headers={'Content-Type':'application/json'}
 if bearer:headers['Authorization']='Bearer '+bearer
 req=urllib.request.Request(BASE+path,data=json.dumps(body).encode() if body is not None else None,headers=headers,method=method)
 try:
  with urllib.request.urlopen(req,timeout=10) as r:status=r.status;data=r.read()
 except urllib.error.HTTPError as e:status=e.code;data=e.read()
 try:val=json.loads(data) if data else None
 except ValueError:val={'bytes':len(data)}
 return status,val
def check(label,method,path,expected,body=None,bearer=None,summary=False):
 status,value=request(method,path,body,bearer)
 assert status==expected,(label,status,mask(value))
 response={'registros':len(value)} if summary and isinstance(value,list) else mask(value)
 cases.append({'caso':label,'metodo':method,'ruta':path,'esperado':expected,'obtenido':status,'respuesta':response})
 return value
def login():return check('Login con renovación','POST','/api/v1/auth/login',200,{'email':env['ADMIN_EMAIL'],'password':env['ADMIN_PASSWORD']})
check('Estado final','GET','/api/v1/public/estado',200)
check('Recurso protegido sin JWT','GET','/api/v1/productos',401)
status,spec=request('GET','/v3/api-docs');assert status==200 and spec['components']['securitySchemes']['bearerAuth']['scheme']=='bearer'
cases.append({'caso':'Esquema OpenAPI Bearer global','metodo':'GET','ruta':'/v3/api-docs','esperado':200,'obtenido':200,'respuesta':{'version':spec['info']['version'],'security':spec['security'],'esquema':spec['components']['securitySchemes']['bearerAuth'],'rutas':len(spec['paths'])}})
auth=login();token=auth['accessToken'];raw=auth['refreshToken']
cat=check('Crear categoría','POST','/api/v1/categorias',201,{'nombre':'APF3-'+suffix},token)['id']
sede=check('Crear sede','POST','/api/v1/sedes',201,{'codigo':'S-'+suffix,'nombre':'Sede académica','direccion':'Dirección simulada'},token)['id']
w=[]
for i in [1,2]:w.append(check('Crear almacén '+str(i),'POST','/api/v1/almacenes',201,{'codigo':f'A{i}-'+suffix,'nombre':'Almacén de prueba '+str(i),'sedeId':sede},token)['id'])
p=check('Crear producto con historial','POST','/api/v1/productos',201,{'codigo':'H-'+suffix,'nombre':'Histórico','unidadMedida':'caja','stockMinimo':5,'categoriaId':cat},token)['id']
mov=check('Registrar entrada','POST','/api/v1/inventario/entradas',201,{'productoId':p,'almacenId':w[0],'cantidad':10,'motivo':'Prueba de conservación APF3'},token)
check('Archivar producto con historial','DELETE',f'/api/v1/productos/{p}',204,bearer=token)
check('Producto archivado fuera del catálogo','GET',f'/api/v1/productos/{p}',404,bearer=token)
check('Archivar almacén con historial','DELETE',f'/api/v1/almacenes/{w[0]}',204,bearer=token)
check('Almacén archivado fuera del catálogo','GET',f'/api/v1/almacenes/{w[0]}',404,bearer=token)
history=check('Historial conservado y consultable','GET','/api/v1/inventario/movimientos',200,bearer=token,summary=True);assert any(x['id']==mov['id'] and x['productoId']==p for x in history)
renewed=check('Renovar y rotar refresh token','POST','/api/v1/auth/refresh',200,{'refreshToken':raw})
check('Rechazar refresh reutilizado','POST','/api/v1/auth/refresh',401,{'refreshToken':raw})
check('Revocar descendiente por replay','POST','/api/v1/auth/refresh',401,{'refreshToken':renewed['refreshToken']})
auth=login();token=auth['accessToken']
check('Cerrar familia de sesión','POST','/api/v1/auth/logout',204,{'refreshToken':auth['refreshToken']})
check('Refresh cerrado rechazado','POST','/api/v1/auth/refresh',401,{'refreshToken':auth['refreshToken']})
role_status,roles=request('GET','/api/v1/roles',bearer=token);assert role_status==200
role=next(x['id'] for x in roles if x['nombre']=='AUDITOR');password=secrets.token_hex(18);email='auditor-'+suffix+'@example.test'
check('Solicitud simulada de acceso AUDITOR','POST','/api/v1/usuarios',201,{'nombre':'Auditor','apellido':'Académico','email':email,'password':password,'rolId':role},token)
aud=check('Ingreso del auditor de prueba','POST','/api/v1/auth/login',200,{'email':email,'password':password})
check('Auditor consulta inventario','GET','/api/v1/inventario',200,bearer=aud['accessToken'],summary=True)
check('Auditor no modifica catálogo','POST','/api/v1/categorias',403,{'nombre':'No permitida'},aud['accessToken'])
for i in range(50):
 status,prod=request('POST','/api/v1/productos',{'codigo':f'C{i}-'+suffix,'nombre':f'Producto de carga {i}','unidadMedida':'caja','stockMinimo':10,'categoriaId':cat},token);assert status==201
 status,_=request('POST','/api/v1/inventario/entradas',{'productoId':prod['id'],'almacenId':w[1],'cantidad':20,'motivo':'Carga sintética para capacidad'},token);assert status==201
cases.append({'caso':'Preparar 50 productos y 50 existencias para carga','esperado':201,'obtenido':201,'respuesta':{'productos_creados':50,'entradas_creadas':50}})
(OUT/'funcionales.json').write_text(json.dumps({'fecha':datetime.datetime.now(datetime.timezone.utc).isoformat(),'base_url':BASE,'casos':cases,'aprobados':len(cases),'fallos':0},ensure_ascii=False,indent=2))
pid=int((R/'.runtime/api.pid').read_text());paths=['/api/v1/productos','/api/v1/inventario','/api/v1/inventario/movimientos','/api/v1/categorias']
for _ in range(20):status,_=request('GET',paths[_%4],bearer=token);assert status==200
def process_sample():
 row=subprocess.check_output(['ps','-p',str(pid),'-o','time=,rss=']).decode().split();clock=row[0].split(':');secs=float(clock[-1])+int(clock[-2])*60+(int(clock[-3])*3600 if len(clock)>2 else 0)
 return secs,int(row[1])/1024
phases=[]
for users in [1,10,25]:
 start=time.perf_counter();deadline=start+15;before_cpu,_=process_sample();samples=[];stop=threading.Event()
 def sampler():
  while not stop.is_set():
   try:samples.append(process_sample()[1])
   except (OSError,ValueError,subprocess.CalledProcessError):pass
   stop.wait(.5)
 t=threading.Thread(target=sampler);t.start()
 def actor(n):
  lat=[];errors=0;i=n
  while time.perf_counter()<deadline:
   stamp=time.perf_counter()
   try:
    status,_=request('GET',paths[i%4],bearer=token)
    if status!=200:errors+=1
   except OSError:errors+=1
   lat.append((time.perf_counter()-stamp)*1000);i+=1;time.sleep(.1)
  return lat,errors
 with concurrent.futures.ThreadPoolExecutor(max_workers=users) as pool:values=list(pool.map(actor,range(users)))
 stop.set();t.join();elapsed=time.perf_counter()-start;after_cpu,rss=process_sample()
 lat=sorted(x for a,_ in values for x in a);errors=sum(e for _,e in values)
 def perc(p):return round(lat[max(0,math.ceil(p*len(lat))-1)],2)
 phase={'usuarios_virtuales':users,'duracion_s':round(elapsed,2),'solicitudes':len(lat),'errores':errors,'error_pct':round(100*errors/len(lat),3),'rps':round(len(lat)/elapsed,2),'p50_ms':perc(.5),'p95_ms':perc(.95),'p99_ms':perc(.99),'cpu_api_pct_un_nucleo':round(100*(after_cpu-before_cpu)/elapsed,2),'rss_api_max_mib':round(max(samples+[rss]),2)}
 phases.append(phase);print(json.dumps(phase),flush=True)
(OUT/'rendimiento.json').write_text(json.dumps({'fecha':datetime.datetime.now(datetime.timezone.utc).isoformat(),'entorno':{'os':platform.platform(),'arquitectura':platform.machine(),'nucleos_logicos':os.cpu_count(),'java_heap_max_mib':512,'postgresql_local':'17.12','base_url':BASE},'metodologia':{'carga':'cerrada de lectura autenticada','usuarios_virtuales':[1,10,25],'duracion_objetivo_s_por_fase':15,'pausa_s_por_usuario':.1,'calentamiento':20,'rutas':paths,'conexiones_http':'urllib HTTP/1.1 sin pool persistente','cpu':'delta de tiempo CPU del proceso Java / duración; 100% = un núcleo','memoria':'RSS del proceso Java, muestreo cada 0.5 s','limitacion':'Prueba exploratoria corta, un único host y dataset pequeño. No mide producción corporativa, crecimiento sostenido ni saturación.'},'fases':phases},ensure_ascii=False,indent=2))
print('Verificación funcional y rendimiento guardados sin credenciales.',flush=True)
