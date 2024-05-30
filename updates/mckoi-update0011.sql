----------------------------
--Steuer zu steuer_id umziehen
----------------------------

ALTER TABLE buchung add steuer_id INT(10) NULL DEFAULT NULL;
ALTER TABLE buchung add CONSTRAINT `fk_buchung_steuer` FOREIGN KEY (`steuer_id`) REFERENCES `steuer` (`id`);

update buchung b join steuer s on s.satz = b.steuer join konto stk on stk.id = s.steuerkonto_id join kontenrahmen kr on kr.id = stk.kontenrahmen_id join geschaeftsjahr gj on gj.id = b.geschaeftsjahr_id join konto sk on sk.id = b.sollkonto_id join konto hk on hk.id = b.habenkonto_id and kr.id = gj.kontenrahmen_id and ((stk.kontotyp_id = 1 and (sk.kontoart_id = 3 or sk.kontoart_id = 5)) or (stk.kontotyp_id = 2 and (hk.kontoart_id = 3 or hk.kontoart_id = 5))) set b.steuer_id = s.id;

----------------------------
---Steuer_id bei templates hinzufuegen
---------------------------
ALTER TABLE buchungstemplate add steuer_id INT(10) NULL DEFAULT NULL;
ALTER TABLE buchungstemplate add CONSTRAINT `fk_buchungstemplate_steuer` FOREIGN KEY (`steuer_id`) REFERENCES `steuer` (`id`);

update buchungstemplate b join steuer s on s.satz = b.steuer join konto stk on stk.id = s.steuerkonto_id join kontenrahmen kr on kr.id = stk.kontenrahmen_id join konto sk on sk.id = b.sollkonto_id join konto hk on hk.id = b.habenkonto_id and kr.id = b.kontenrahmen_id and ((stk.kontotyp_id = 1 and (sk.kontoart_id = 3 or sk.kontoart_id = 5)) or (stk.kontotyp_id = 2 and (hk.kontoart_id = 3 or hk.kontoart_id = 5))) set b.steuer_id = s.id;

COMMIT;