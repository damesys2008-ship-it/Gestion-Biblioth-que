-- Emprunts (id 1 à 3). L'emprunt 1 est rendu ; les emprunts 2 et 3 sont en retard.
INSERT INTO emprunts (livre_id, emprunteur, email_emprunteur, date_emprunt, date_retour_prevue,
date_retour_effective, rendu) VALUES
(1, 'Marie Dupont', 'marie.dupont@email.com', '2026-09-01', '2026-09-15', '2026-09-10', TRUE),
(3, 'Jean Martin', 'jean.martin@email.com', '2026-09-05', '2026-09-19', NULL, FALSE),
(5, 'Sophie Bernard', 'sophie.bernard@email.com', '2026-09-20', '2026-10-04', NULL, FALSE);
