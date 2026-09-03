const API_BASE = '/api';
const OFFLINE_AFTER_MS = 90_000;
const users = ['sepehr','amir'];

function duration(ms){
  if(!Number.isFinite(ms) || ms < 0) return '—';
  const s=Math.floor(ms/1000), d=Math.floor(s/86400), h=Math.floor(s%86400/3600), m=Math.floor(s%3600/60), sec=s%60;
  return [d?`${d}روز`:null,h?`${h}ساعت`:null,m?`${m}دقیقه`:null,`${sec}ثانیه`].filter(Boolean).join(' ');
}
function render(user, data){
  const live = data?.lastSeen && Date.now()-new Date(data.lastSeen).getTime() < OFFLINE_AFTER_MS;
  document.getElementById(`${user}-card`).classList.toggle('live', !!live);
  document.getElementById(`${user}-network`).textContent = live ? (data.internet ? 'متصل' : 'قطع') : 'آفلاین';
  document.getElementById(`${user}-screen`).textContent = live ? (data.screenOn ? 'روشن' : 'خاموش') : 'نامشخص';
  const since = data.onlineSince ? Date.now()-new Date(data.onlineSince).getTime() : NaN;
  document.getElementById(`${user}-online`).textContent = live ? duration(since) : 'آفلاین';
  document.getElementById(`${user}-last`).textContent = live ? `آخرین heartbeat: ${new Date(data.lastSeen).toLocaleTimeString('fa-IR')}` : 'اتصال فعالی دیده نشد';
}
async function refresh(){
  try{
    const res=await fetch(`${API_BASE}/status`,{cache:'no-store'});
    if(!res.ok) throw new Error('API');
    const payload=await res.json();
    users.forEach(u=>render(u,payload[u]||{}));
    document.getElementById('connection').classList.add('live');
    document.querySelector('#connection span').textContent='اتصال زنده';
    document.getElementById('updated').textContent=`به‌روزرسانی ${new Date().toLocaleTimeString('fa-IR')}`;
  }catch(e){
    document.getElementById('connection').classList.remove('live');
    document.querySelector('#connection span').textContent='API متصل نیست';
  }
}
refresh(); setInterval(refresh,5000); setInterval(()=>refresh(),30000);
