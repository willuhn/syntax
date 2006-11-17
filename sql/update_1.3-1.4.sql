-- per 01.01.2007: 19% Mehrwertsteuer

------------------------------------------------
-- SKR03

-- Neue Steuersaetze
#set($id1 = $sql.newID("steuer"))
#set($id2 = $id1+1)
INSERT INTO steuer (id,name, satz, steuerkonto_id) VALUES (${id1},'Vorsteuer 19%', '16', 1);
INSERT INTO steuer (id,name, satz, steuerkonto_id) VALUES (${id2},'Umsatzsteuer 19%', '16', 3);

-- Vorsteuer auf neuen Default-Steuersatz aendern
UPDATE konto SET steuer_id=${id1} where steuer_id=1;

-- UST auf neuen Default-Steuersatz aendern
UPDATE konto SET steuer_id=${id2} where steuer_id=3;

-- Erloeskonten anpassen
update konto set name='Erlöse (keine UST)' WHERE ID=206;
update konto set name='Erlöse (ermäßigte UST)' WHERE ID=207;
update konto set name='Erlöse (volle UST)' WHERE ID=208;

------------------------------------------------
-- SKR04

-- Neue Steuersaetze
#set($id3 = $id1+2)
#set($id4 = $id1+3)
INSERT INTO steuer (id,name, satz, steuerkonto_id) VALUES (${id3},'Vorsteuer 19%', '19', 1001);
INSERT INTO steuer (id,name, satz, steuerkonto_id) VALUES (${id4},'Umsatzsteuer 19%', '19', 1003);

-- Vorsteuer auf neuen Default-Steuersatz aendern
UPDATE konto SET steuer_id=${id3} where steuer_id=1001;

-- UST auf neuen Default-Steuersatz aendern
UPDATE konto SET steuer_id=${id4} where steuer_id=1003;

-- Erloeskonten anpassen
update konto set name='Erlöse (keine UST)' WHERE ID=1204;
update konto set name='Erlöse (ermäßigte UST)' WHERE ID=1205;
update konto set name='Erlöse (volle UST)' WHERE ID=1206;
