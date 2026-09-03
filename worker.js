// Cloudflare Worker + D1 API for the Sepehr/Amir live status dashboard.
// Bind a D1 database as DB and set STATUS_TOKEN as a Worker secret.

const CORS = { 'content-type':'application/json; charset=utf-8', 'access-control-allow-origin':'*', 'access-control-allow-headers':'content-type,x-status-token', 'access-control-allow-methods':'GET,POST,OPTIONS' };
const json=(data,status=200)=>new Response(JSON.stringify(data),{status,headers:CORS});

export default {
  async fetch(request, env){
    if(request.method==='OPTIONS') return new Response('',{headers:CORS});
    const url=new URL(request.url);
    if(url.pathname==='/api/status' && request.method==='GET'){
      const rows=await env.DB.prepare('SELECT user,last_seen,internet,screen_on,online_since FROM status').all();
      const out={}; for(const r of rows.results||[]) out[r.user]={lastSeen:r.last_seen,internet:!!r.internet,screenOn:!!r.screen_on,onlineSince:r.online_since};
      return json(out);
    }
    if(url.pathname==='/api/heartbeat' && request.method==='POST'){
      if(request.headers.get('x-status-token')!==env.STATUS_TOKEN) return json({error:'unauthorized'},401);
      const b=await request.json();
      if(!['sepehr','amir'].includes(b.user)) return json({error:'invalid user'},400);
      const now=new Date().toISOString();
      await env.DB.prepare(`INSERT INTO status(user,last_seen,internet,screen_on,online_since) VALUES(?,?,?,?,COALESCE(?,?)) ON CONFLICT(user) DO UPDATE SET last_seen=excluded.last_seen,internet=excluded.internet,screen_on=excluded.screen_on,online_since=excluded.online_since`).bind(b.user,now,b.internet?1:0,b.screenOn?1:0,b.onlineSince||null,now).run();
      return json({ok:true,at:now});
    }
    return new Response('Not found',{status:404});
  }
};
