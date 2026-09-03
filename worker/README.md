# Phone Presence Worker

Cloudflare Worker + D1 API for Sepehr/Amir presence.

## Cloudflare steps

1. Create a D1 database named `phone-presence`.
2. Put its ID in `worker/wrangler.toml`.
3. Run `wrangler d1 execute phone-presence --remote --file=worker/schema.sql`.
4. Deploy the Worker from `worker/`.
5. Create secrets:
   - `PRESENCE_WRITE_TOKEN`
   - `PRESENCE_READ_TOKEN`

Never commit real tokens to GitHub.
