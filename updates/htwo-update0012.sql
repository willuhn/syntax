----------------------------
--Steuer zu steuer_id umziehen
----------------------------

ALTER TABLE buchung add steuer_id INT(10) NULL;
ALTER TABLE buchung add CONSTRAINT fk_buchung_steuer FOREIGN KEY (steuer_id) REFERENCES steuer (id) DEFERRABLE;

----------------------------
--Fehlende Steuersaetze anlegen
----------------------------
INSERT INTO steuer  (name, satz, steuerkonto_id,ust_nr_steuer,ust_nr_bemessung) SELECT DISTINCT CONCAT(CASE WHEN stk.kontotyp_id = 1 THEN 'Umsatzsteuer' ELSE 'Vorsteuer' END,b.steuer,'%'), b.steuer,s.steuerkonto_id,s.ust_nr_steuer,s.ust_nr_bemessung FROM buchung b, konto k, steuer s, konto stk, geschaeftsjahr gj WHERE b.steuer is not null and ((k.id = b.habenkonto_id and (k.kontoart_id != 3 and k.kontoart_id != 5)) or (k.id = b.sollkonto_id and (k.kontoart_id != 3 and k.kontoart_id != 5))) and s.steuerkonto_id is not null and s.id = k.steuer_id and stk.id = s.steuerkonto_id and gj.id = b.geschaeftsjahr_id and concat(stk.kontenrahmen_id,stk.kontotyp_id, b.steuer) not in (SELECT DISTINCT concat(stk2.kontenrahmen_id,stk2.kontotyp_id, s2.satz) FROM steuer s2,konto stk2 WHERE stk2.kontotyp_id is not null and (s2.mandant_id = gj.mandant_id or s2.mandant_id is null) and s2.steuerkonto_id = stk2.id);

----------------------------
---Steuer_id bei templates hinzufuegen
---------------------------
ALTER TABLE buchungstemplate add steuer_id INT(10) NULL;
ALTER TABLE buchungstemplate add CONSTRAINT fk_buchungstemplate_steuer FOREIGN KEY (steuer_id) REFERENCES steuer (id) DEFERRABLE;

----------------------------
--Fehlende Steuersaetze anlegen
----------------------------
INSERT INTO steuer  (name, satz, steuerkonto_id,ust_nr_steuer,ust_nr_bemessung) SELECT DISTINCT CONCAT(CASE WHEN stk.kontotyp_id = 1 THEN 'Umsatzsteuer' ELSE 'Vorsteuer' END,b.steuer,'%'), b.steuer,s.steuerkonto_id,s.ust_nr_steuer,s.ust_nr_bemessung FROM buchungstemplate b, konto k, steuer s, konto stk WHERE b.steuer is not null and ((k.id = b.habenkonto_id and (k.kontoart_id != 3 and k.kontoart_id != 5)) or (k.id = b.sollkonto_id and (k.kontoart_id != 3 and k.kontoart_id != 5))) and s.steuerkonto_id is not null and s.id = k.steuer_id and stk.id = s.steuerkonto_id and concat(stk.kontenrahmen_id,stk.kontotyp_id, b.steuer) not in (SELECT DISTINCT concat(stk2.kontenrahmen_id,stk2.kontotyp_id, s2.satz) FROM steuer s2,konto stk2 WHERE stk2.kontotyp_id is not null and (s2.mandant_id = b.mandant_id or s2.mandant_id is null) and s2.steuerkonto_id = stk2.id);

COMMIT;
