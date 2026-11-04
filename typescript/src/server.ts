import Fastify from "fastify";
import { loadConfig } from "./config.js";
import { connect } from "./db.js";

const config = loadConfig();
const app = Fastify({ logger: true });

app.get("/healthz", async () => ({ status: "ok" }));
app.setNotFoundHandler((_req, reply) => {
  reply.code(404).send({ code: "NOT_FOUND", message: "no such route" });
});

const pool = await connect(config.databaseUrl);
app.addHook("onClose", async () => pool.end());

await app.listen({ port: config.port, host: "0.0.0.0" });
app.log.info(`send service listening on :${config.port} (stub ${config.stubUrl})`);
