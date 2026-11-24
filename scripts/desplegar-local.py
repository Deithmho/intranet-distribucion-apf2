#!/usr/bin/env python3
"""Despliegue académico aislado con Java 21 y PostgreSQL 17 (macOS/Linux)."""
from pathlib import Path
import os,sys,subprocess,secrets,base64,shutil,time,urllib.request,signal
R=Path(__file__).resolve().parents[1];W=R/'.runtime';W.mkdir(mode=0o700,exist_ok=True)
pg=Path(os.environ.get('PG_BIN','/opt/homebrew/opt/postgresql@17/bin'))
if not (pg/'pg_ctl').exists():
 found=shutil.which('pg_ctl')
 if found:pg=Path(found).parent
 else:sys.exit('Instala PostgreSQL 17 y define PG_BIN con su directorio bin.')
if '--stop' in sys.argv:
 pidfile=W/'api.pid'
 if pidfile.exists():
  pid=int(pidfile.read_text());cmd=subprocess.run(['ps','-p',str(pid),'-o','command='],capture_output=True,text=True).stdout
  if str(R/'build/libs') in cmd:os.kill(pid,signal.SIGTERM)
  pidfile.unlink(missing_ok=True)
 subprocess.run([str(pg/'pg_ctl'),'-D',str(W/'postgres'),'stop','-m','fast'])
 sys.exit(0)
envf=R/'.env'
if not envf.exists():
 v={'DB_URL':'jdbc:postgresql://127.0.0.1:55433/intranet','DB_USERNAME':'intranet','DB_PASSWORD':secrets.token_hex(24),'JWT_SECRET':base64.b64encode(secrets.token_bytes(48)).decode(),'JWT_EXPIRATION':'900000','BOOTSTRAP_ENABLED':'true','ADMIN_EMAIL':'admin@example.test','ADMIN_PASSWORD':secrets.token_hex(20),'CORS_ORIGINS':'http://localhost:8083','SERVER_PORT':'8083'}
 envf.write_text('\n'.join(k+'='+v for k,v in v.items())+'\n');envf.chmod(0o600)
v=dict(l.split('=',1) for l in envf.read_text().splitlines() if '=' in l and not l.startswith('#'))
env=os.environ.copy();env.update(v);env.update(DB_URL='jdbc:postgresql://127.0.0.1:55433/intranet',SERVER_PORT='8083',PGPASSWORD=v['DB_PASSWORD'])
if not (W/'postgres/PG_VERSION').exists():
 pw=W/'db-password';pw.write_text(v['DB_PASSWORD']);pw.chmod(0o600)
 try:subprocess.run([str(pg/'initdb'),'-D',str(W/'postgres'),'-U',v['DB_USERNAME'],'--auth-host=scram-sha-256','--auth-local=scram-sha-256','--pwfile',str(pw)],check=True)
 finally:pw.unlink(missing_ok=True)
status=subprocess.run([str(pg/'pg_ctl'),'-D',str(W/'postgres'),'status'],stdout=subprocess.DEVNULL)
if status.returncode:subprocess.run([str(pg/'pg_ctl'),'-D',str(W/'postgres'),'-l',str(W/'postgres.log'),'-o','-p 55433 -h 127.0.0.1 -k /tmp','start'],check=True)
base=[str(pg/'psql'),'-h','127.0.0.1','-p','55433','-U',v['DB_USERNAME'],'-d','postgres','-tAc']
if subprocess.check_output(base+["SELECT 1 FROM pg_database WHERE datname='intranet'"],env=env).strip()!=b'1':subprocess.run([str(pg/'createdb'),'-h','127.0.0.1','-p','55433','-U',v['DB_USERNAME'],'intranet'],env=env,check=True)
try:
 urllib.request.urlopen('http://127.0.0.1:8083/api/v1/public/estado',timeout=2)
 sys.exit('Ya hay un servicio en el puerto 8083. Detén la instancia anterior antes de iniciar otra.')
except (OSError,urllib.error.URLError):pass
subprocess.run([str(R/'gradlew'),'bootJar','--no-daemon'],cwd=R,check=True)
jars=list((R/'build/libs').glob('*0.3.0.jar'));assert len(jars)==1
with (W/'api.log').open('ab') as log:p=subprocess.Popen(['java','-Xms128m','-Xmx512m','-jar',str(jars[0])],cwd=R,env=env,stdout=log,stderr=log,start_new_session=True)
(W/'api.pid').write_text(str(p.pid))
for _ in range(60):
 try:
  urllib.request.urlopen('http://127.0.0.1:8083/api/v1/public/estado',timeout=1)
  print('API: http://127.0.0.1:8083 | Swagger: http://127.0.0.1:8083/swagger-ui/index.html');sys.exit(0)
 except OSError:time.sleep(1)
sys.exit('Revisa .runtime/api.log: el arranque no completó en 60 s.')
