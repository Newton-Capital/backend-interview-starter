/* eslint-disable camelcase */
// Your schema goes here. The scaffold creates no tables: the data model is yours.
// node-pg-migrate applies files in this directory in order; add the next one with
// `npx node-pg-migrate create <name>` (keep the .cjs extension: the package is ESM, migrations are loaded with require).

exports.shorthands = undefined;

/** @param {import('node-pg-migrate').MigrationBuilder} pgm */
exports.up = (pgm) => {
  pgm.sql("select 1");
};

/** @param {import('node-pg-migrate').MigrationBuilder} pgm */
exports.down = (pgm) => {
  pgm.sql("select 1");
};
