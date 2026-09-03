const cors = {"Access-Control-Allow-Origin":"*","Access-Control-Allow-Headers":"Content-Type, Authorization","Access-Control-Allow-Methods":"GET,POST,OPTIONS"};
const json = (data,status=200)=>new Response(JSON.stringify(data),{status,headers:{...cors,"Content-Type":"application/json"}});

export default {
  async fetch(request, env) {
    if (request.method === "OPTIONS") return new Response(null,{headers:cors});
    const url = new URL(request.url);
    if (url.pathname === "/api/presence" && request.method === "POST") {
      const auth = request.headers.get("Authorization") || "";
      if (auth !== `Bearer ${env.PRESENCE_WRITE_TOKEN}`) return json({error:"Unauthorized"},401);
      const b = await request.json();
      if (!["sepehr","amir"].includes(b.userId)) return json({error:"Invalid userId"},400);
      const now = Date.now();
      await env.DB.prepare(`INSERT INTO presence (user_id,internet_online,screen_on,last_seen) VALUES (?1,?2,?3,?4) ON CONFLICT(user_id) DO UPDATE SET internet_online=excluded.internet_online,screen_on=excluded.screen_on,last_seen=excluded.last_seen`).bind(b.userId,b.internetOnline?1:0,b.screenOn?1:0,now).run();
      return json({ok:true});
    }
    if (url.pathname === "/api/presence" && request.method === "GET") {
      const auth = request.headers.get("Authorization") || "";
      if (auth !== `Bearer ${env.PRESENCE_READ_TOKEN}`) return json({error:"Unauthorized"},401);
      const rows = await env.DB.prepare("SELECT user_id,internet_online,screen_on,last_seen FROM presence").all();
      return json({users:rows.results || []});
    }
    return json({error:"Not found"},404);
  }
};
