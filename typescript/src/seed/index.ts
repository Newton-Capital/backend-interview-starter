import { loadConfig } from "../config.js";
import { connect } from "../db.js";
import { insert, NotImplemented } from "./insert.js";
import { loadSeed } from "./load.js";

const config = loadConfig();
const data = await loadSeed(config.seedDir);
console.log(`seed: ${data.wallets.length} wallets, ${data.quotes.length} quotes, fee ${data.fees.wallet.feePercent}% parsed from ${config.seedDir}`);

const pool = await connect(config.databaseUrl);
try {
  await insert(pool, data);
  console.log("seed: loaded");
} catch (err) {
  if (err instanceof NotImplemented) {
    console.error(`seed: ${err.message}`);
    process.exit(2);
  }
  throw err;
} finally {
  await pool.end();
}
