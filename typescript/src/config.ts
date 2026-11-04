import { existsSync } from "node:fs";

/** Everything the service needs from outside. */
export interface Config {
  port: number;
  databaseUrl: string;
  stubUrl: string;
  seedDir: string;
}

export function loadConfig(): Config {
  return {
    port: Number(process.env.PORT ?? 8080),
    databaseUrl: process.env.DATABASE_URL ?? "postgres://interview:interview@localhost:5432/send?sslmode=disable",
    stubUrl: process.env.STUB_URL ?? "http://localhost:9000",
    seedDir: process.env.SEED_DIR ?? firstDir("../seed", "../../seed"),
  };
}

function firstDir(...candidates: string[]): string {
  return candidates.find((c) => existsSync(c)) ?? candidates[0];
}
