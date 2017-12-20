3.0.0 / WIP
==================

Contract breaking changes:
* [OLMIS-3295](https://openlmis.atlassian.net/browse/OLMIS-3295): Modified stock event structure
  * adjustments in each stock event line item contains only reasonId and quantity fields

Improveemnts:
* [OLMIS-3614](https://openlmis.atlassian.net/browse/OLMIS-3614): Added extraData field to Stock Card Summary.

Bug fixes, security and performance improvements, also backwards-compatible:

* [OLMIS-3533](https://openlmis.atlassian.net/browse/OLMIS-3533): Avoid creating duplicate stock cards for the same pair of orderable and lot
* [OLMIS-3485](https://openlmis.atlassian.net/browse/OLMIS-3485): Fixed jasper report for stock view and summaries
* [OLMIS-3135](https://openlmis.atlassian.net/browse/OLMIS-3135): Handle API Key requests.
  * For now all requests are blocked.

2.0.0 / 2017-11-09
==================

Contract breaking changes:

* [OLMIS-2732](https://openlmis.atlassian.net/browse/OLMIS-2732): Print submitted physical inventory
  * During work on this ticket physical inventory API was redesigned to be RESTful.

New functionality that are backwards-compatible
* [OLMIS-3246](https://openlmis.atlassian.net/browse/OLMIS-3246): Add column hidden to valid reasons
  * Default value is false.
  * Updated demo data so reason assignments for Consumed, Receipts, Beginning Balance Excess and Beginning Balance Insufficiency are hidden.

Bug fixes, security and performance improvements, also backwards-compatible:

* [OLMIS-3148](https://openlmis.atlassian.net/browse/OLMIS-3148): Added missing messages for error keys
* [OLMIS-3346](https://openlmis.atlassian.net/browse/OLMIS-3346): Increase performance of POST /stockEvents endpoint by reducing db calls and use lazy-loading in the stock event process context. Also changed logic for notification of stockout to asynchronous.

1.0.0 / 2017-09-01
==================

* Released openlmis-stockmanagement 1.0.0 as part of openlmis-ref-distro 3.2.0.
 * This was the first stable release of openlmis-stockmanagement.
