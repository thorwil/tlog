# Preparing Postgresql

Password for user "postgres":
   sudo -u postgres psql postgres
   \password postgres;

Create "tlog" database:
   sudo -u postgres createdb tlog

Run psql, connect to "tlog" database:
   sudo -u postgres psql tlog

Connect to a database in psql:
   \connect tlog;


# Setting up tables

CREATE TABLE account (
   username text PRIMARY KEY,
   password text NOT NULL);

REPL:
   tlog.init> (creds/hash-bcrypt "password")
   "$2a$10$Cp0GK58KbTE3SYx8wmgqLO2oF2JHLp/a72D6UGevvKCwfzdPHqBHu"

INSERT INTO account VALUES
   ('admin', '$2a$10$Cp0GK58KbTE3SYx8wmgqLO2oF2JHLp/a72D6UGevvKCwfzdPHqBHu');

CREATE TABLE feed (
   position smallint NOT NULL, -- To get feeds in a fixed order, 0 ... n
   slug text PRIMARY KEY,
   preset boolean NOT NULL); -- On whether the feed is selected by default on the new article form

INSERT INTO feed VALUES
   (0, 'journal', true),
   (1, 'planet-ubuntu', true), -- http://planet.ubuntu.com/
   (2, 'planet-linuxaudio', false), -- ;; http://planet.linuxaudio.org/
   (3, 'graphicsplanet', false); -- http://www.graphicsplanet.org/

CREATE TABLE ressource (
   slug text PRIMARY KEY,
   created_timestamp timestamp,
   updated_timestamp timestamp,
   table_reference text);

CREATE TABLE article (
   slug text PRIMARY KEY REFERENCES ressource(slug),
   title text,
   body text);
