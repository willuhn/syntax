-- per 01.01.2007: 19% Mehrwertsteuer

------------------------------------------------
-- SKR03

#set($id = ${id.newID("steuer")})

-- Neue Steuersaetze
INSERT INTO steuer (id,name, satz, steuerkonto_id) VALUES ($id,'Vorsteuer 19%', '16', 1);
INSERT INTO steuer (id,name, satz, steuerkonto_id) VALUES ($id+1,'Umsatzsteuer 19%', '16', 3);

-- Vorsteuer auf neuen Default-Steuersatz aendern
UPDATE konto SET steuer_id=8 where steuer_id=1;

-- UST auf neuen Default-Steuersatz aendern
UPDATE konto SET steuer_id=9 where steuer_id=3;

-- Erloeskonten anpassen
update konto set name='Erlöse (keine UST)' WHERE ID=206;
update konto set name='Erlöse (ermäßigte UST)' WHERE ID=207;
update konto set name='Erlöse (volle UST)' WHERE ID=208;

------------------------------------------------
-- SKR04

-- Neue Steuersaetze
INSERT INTO steuer (id,name, satz, steuerkonto_id) VALUES (1008,'Vorsteuer 19%', '19', 1001);
INSERT INTO steuer (id,name, satz, steuerkonto_id) VALUES (1009,'Umsatzsteuer 19%', '19', 1003);

-- Vorsteuer auf neuen Default-Steuersatz aendern
UPDATE konto SET steuer_id=1008 where steuer_id=1001;

-- UST auf neuen Default-Steuersatz aendern
UPDATE konto SET steuer_id=1009 where steuer_id=1003;

-- Erloeskonten anpassen
update konto set name='Erlöse (keine UST)' WHERE ID=1204;
update konto set name='Erlöse (ermäßigte UST)' WHERE ID=1205;
update konto set name='Erlöse (volle UST)' WHERE ID=1206;
