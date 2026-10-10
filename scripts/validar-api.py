#!/usr/bin/env python3
"""Pruebas reproducibles mediante cURL; guarda solo evidencias redactadas."""
import base64,datetime,json,os,subprocess,sys,uuid
from pathlib import Path
from urllib.parse import urlparse
BASE=os.getenv('API_URL','http://localhost:8080').rstrip('/')
if urlparse(BASE).hostname not in ('localhost','127.0.0.1'):
    raise SystemExit('Este script de datos sintéticos está limitado al entorno local.')
OUT=Path(os.getenv('EVIDENCE_DIR','docs/evidencias'));OUT.mkdir(parents=True,exist_ok=True)
TOKEN='';CASES=[];SUFFIX=uuid.uuid4().hex[:6]
def clean(x):
    if isinstance(x,dict):return {k:('[OMITIDO]' if k in ('password','accessToken','token','secret') else clean(v)) for k,v in x.items()}
    if isinstance(x,list):return [clean(v) for v in x]
    return x
def req(label,method,path,payload=None,expected=200,token=None,authenticated=True):
    use=TOKEN if token is None else token
    cfg=['url = '+json.dumps(BASE+'/api/v1/'+path),'request = '+json.dumps(method),'silent','show-error','write-out = "\\n%{http_code}"','max-time = 20']
    if authenticated and use:cfg.append('header = '+json.dumps('Authorization: Bearer '+use))
    if payload is not None:
        cfg.append('header = "Content-Type: application/json"');cfg.append('data = '+json.dumps(json.dumps(payload,ensure_ascii=False)))
    result=subprocess.run(['curl','--config','-'],input='\n'.join(cfg)+'\n',capture_output=True,text=True,check=True)
    body,code=result.stdout.rsplit('\n',1);status=int(code)
    try:data=json.loads(body)
    except json.JSONDecodeError:data=body
    record={'caso':label,'metodo':method,'ruta':'/api/v1/'+path,'authorization':'Bearer [OMITIDO]' if authenticated and use else 'sin token','solicitud':clean(payload),'esperado':expected,'http':status,'resultado':'OK' if status==expected else 'FALLO','respuesta':clean(data)}
    CASES.append(record)
    if status!=expected:
        save();raise AssertionError(label+': HTTP '+str(status)+' esperado '+str(expected))
    return data

def save():
    report={'fecha':datetime.datetime.now(datetime.timezone(datetime.timedelta(hours=-5))).isoformat(),'entorno':BASE,'herramienta':'cURL','casos':CASES,'total':len(CASES),'correctos':sum(x['resultado']=='OK' for x in CASES)}
    (OUT/'resultados-api.json').write_text(json.dumps(report,ensure_ascii=False,indent=2))
    lines=['# Evidencias de API APF2','',f"Fecha: {report['fecha']}",f"Entorno: {BASE}",'Herramienta: cURL. Contraseñas y tokens omitidos. Datos sintéticos.','']
    for i,c in enumerate(CASES,1):
        lines.extend([f"## {i:02d} {c['caso']}",f"{c['metodo']} {c['ruta']}",f"Authorization: {c['authorization']}",f"HTTP {c['http']} | esperado {c['esperado']} | {c['resultado']}",'```json',json.dumps(c['respuesta'],ensure_ascii=False,indent=2),'```',''])
    (OUT/'resultados-api.md').write_text('\n'.join(lines))

email=os.environ['ADMIN_EMAIL'];password=os.environ['ADMIN_PASSWORD']
req('Acceso protegido sin JWT','GET','productos',expected=401,authenticated=False)
auth=req('Inicio de sesión y generación JWT','POST','auth/login',{'email':email,'password':password},authenticated=False);TOKEN=auth['accessToken']
header=TOKEN.split('.')[0];jwt_header=json.loads(base64.urlsafe_b64decode(header+'='*((-len(header))%4)))
(OUT/'jwt-metadatos.json').write_text(json.dumps({'algoritmo':jwt_header['alg'],'tokenType':auth['tokenType'],'token_publicado':False},indent=2))
req('Mismo endpoint con JWT válido','GET','productos')
req('JWT malformado','GET','productos',expected=401,token='invalido')
parts=TOKEN.split('.');sig=parts[2];parts[2]=('A' if sig[0]!='A' else 'B')+sig[1:]
req('Firma JWT alterada','GET','productos',expected=401,token='.'.join(parts))
category=req('Crear categoría','POST','categorias',{'nombre':'Bebidas-'+SUFFIX,'descripcion':'Datos sintéticos APF2'},201)['id']
sede=req('Crear sede','POST','sedes',{'codigo':'S-'+SUFFIX,'nombre':'Sede académica Lima','direccion':'Dirección de demostración'},201)['id']
a=req('Crear almacén origen','POST','almacenes',{'codigo':'A-'+SUFFIX,'nombre':'Almacén origen','sedeId':sede},201)['id']
b=req('Crear almacén destino','POST','almacenes',{'codigo':'B-'+SUFFIX,'nombre':'Almacén destino','sedeId':sede},201)['id']
prod={'codigo':'P-'+SUFFIX,'nombre':'Producto demostrativo','unidadMedida':'caja','stockMinimo':10,'categoriaId':category}
p=req('Crear producto','POST','productos',prod,201)['id']
req('Consultar producto','GET','productos/'+str(p))
prod['nombre']='Producto actualizado';req('Actualizar producto','PUT','productos/'+str(p),prod)
req('Búsqueda JPQL por categoría y nombre','GET',f'productos?categoriaId={category}&texto=actualizado')
req('Rechazar categoría referenciada','DELETE','categorias/'+str(category),expected=409)
req('Rechazar código duplicado','POST','productos',prod,409)
req('Rechazar datos inválidos','POST','productos',{},400)
req('Entrada de existencias','POST','inventario/entradas',{'productoId':p,'almacenId':a,'cantidad':20,'motivo':'Ingreso académico'},201)
req('Traslado atómico entre almacenes','POST','inventario/traslados',{'productoId':p,'origenId':a,'destinoId':b,'cantidad':7,'motivo':'Despacho académico'},201)
req('Consultar stock origen después del traslado','GET',f'inventario?almacenId={a}')
req('Consulta JPQL de stock crítico','GET',f'inventario?almacenId={b}&categoriaId={category}&critico=true')
req('Rechazar traslado por stock insuficiente','POST','inventario/traslados',{'productoId':p,'origenId':a,'destinoId':b,'cantidad':99,'motivo':'Prueba de integridad'},409)
req('Rollback con destino inexistente','POST','inventario/traslados',{'productoId':p,'origenId':a,'destinoId':999999999,'cantidad':3,'motivo':'Prueba de rollback'},404)
remaining=req('Stock conservado tras la reversión','GET',f'inventario?almacenId={a}');assert next(x['cantidad'] for x in remaining if x['productoId']==p)==13
req('Historial de movimientos','GET','inventario/movimientos')
rolelist=req('Roles y permisos','GET','roles');auditor=next(r['id'] for r in rolelist if r['nombre']=='AUDITOR')
audit_password=uuid.uuid4().hex;auditemail='auditor-'+SUFFIX+'@example.test'
u=req('Administrador crea auditor','POST','usuarios',{'nombre':'Auditor','apellido':'Académico','email':auditemail,'password':audit_password,'rolId':auditor},201)['id']
audit_token=req('Inicio de sesión del auditor','POST','auth/login',{'email':auditemail,'password':audit_password},authenticated=False)['accessToken']
req('Auditor consulta inventario','GET','inventario',token=audit_token)
req('Auditor no puede modificar catálogo','POST','categorias',{'nombre':'No permitida'},403,token=audit_token)
req('Auditor no puede administrar usuarios','GET','usuarios',expected=403,token=audit_token)
req('Administrador elimina usuario de prueba','DELETE','usuarios/'+str(u),expected=204)
req('Token de usuario eliminado es rechazado','GET','productos',expected=401,token=audit_token)
temp=req('Crear producto para comprobar eliminación','POST','productos',{**prod,'codigo':'T-'+SUFFIX},201)['id']
req('Eliminar producto sin referencias','DELETE','productos/'+str(temp),expected=204)
req('Registro eliminado retorna 404','GET','productos/'+str(temp),expected=404)
req('Rechazar eliminación de producto con historial','DELETE','productos/'+str(p),expected=409)
save();print(f'{len(CASES)}/{len(CASES)} casos correctos. Evidencias redactadas en {OUT}')
