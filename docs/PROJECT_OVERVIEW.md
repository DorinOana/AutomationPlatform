# AutomationPlatform – Big Picture

## Scop
Framework Java de automation consumat ca dependency in proiecte de test (API/UI/mixte). Obiectiv: proiectul consumator sa adauge **o singura dependenta** (testkit) si sa primeasca configurari si tool-uri standard (JUnit, Allure, logging, etc.) fara setup manual.

## Arhitectura (confirmata)
- `automationplatform-deps` (parent POM, intern)
  - `dependencyManagement` pentru versiuni
  - dependinte comune cu `scope=test` pentru JUnit + Allure
- `automationplatform-bom` (contract extern)
  - versiuni sincronizate din deps
- `automationplatform-core`
  - infrastructura comuna + helpers Allure
- `automationplatform-api`
  - REST testing (RestAssured)
- (urmeaza) `ui`, `wiremock`, `kafka`, `pact`

## Filosofie de livrare
- **BOM** = doar versiuni dependente (nu poate livra pluginuri).
- **Pluginuri build** (Surefire/Allure) trebuie gestionate la nivel de parent (pentru consumatori).
- Tinta: un modul `automationplatform-testkit` (agregator) care aduce toate capabilitatile relevante printr-o singura dependenta.

## Allure (centralizat)
- Rezultatele se scriu in **root**: `target/allure-results`.
- Raportul HTML se genereaza in **root**: `target/allure-report`.
- Generarea raportului este intr-un profil Maven `allure`:
  - Ruleaza doar la `-Pallure`.
  - Agregarea ruleaza doar in root (`inherited=false`).

### Comanda locala
```bash
mvn clean install -Pallure
```

## CI (GitHub Actions)
- Testele ruleaza separat de generarea raportului.
- Raportul se genereaza cu `mvn -DskipTests -Pallure verify`.
- Upload Allure results/report din `target/allure-*`.

## Reguli de lucru
- Lucram pas cu pas.
- O singura varianta recomandata la fiecare pas.
- Nu mutam responsabilitati intre module fara explicarea impactului.
- Daca apare o contradictie, o semnalam inainte de a continua.

## Next steps (confirmate anterior)
1) Adaugare `automationplatform-parent` (extern) pentru pluginuri si configurare build.
2) Adaugare `automationplatform-testkit` (o singura dependenta pentru consumatori).

## Work log (ziua curenta)
- 2026-01-31: Allure centralizat in root, profil `allure` pentru agregare + report, CI actualizat; scriptul de sync deps->bom stabilizat.
