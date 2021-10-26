# Desktop

## Services

### BTC-Fiat DEX

#### Crypto Supported
Only Bitcoin (BTC) is supported.

Increments available are based on daily (UT) volume:

* 0/day: 0.01
* 30/day: 0.001, 0.01
* 60/day: 0.001, 0.01, 0.1
* 90/day: 0.001, 0.01, 0.1, 1

#### Fiat Supported
* AFN - Afghan Afghani
* CNY - Chinese Yuan
* CRC - Costa Rican Colon
* EGP - Egyptian Pound
* EUR - European
* GBP - British Pound
* IDR - Indonesian Rupiah
* INR - Indian Rupee
* IQD - Iraqi Dinar
* IRR - Iranian Rial
* ISK - Icelandic Krona
* JPY - Japanese Yen
* KPW - North Korean Won
* KRW - South Korean Won
* LBP - Lebanese Pound
* MXN - Mexican Peso
* NGN - Nigerian Naira
* RUB - Russian Ruble
* SAR - Saudi Riyal
* USD - US Dollar

#### Exchange Methods Supported
* Fiat
    * Advanced Cash
    * AliPay
    * Amazon eGift Card
    * Austrialian PayID
    * US Postal Money Order
    * Popmoney
    * Revolut
    * SEPA
    * WeChat Pay
    * Zelle (ClearXchange)
* Fiat & Other
    * Face-to-Face (In-Person)
    * Mail

#### Fees
* DEX - 0.5%
* External networks (e.g. Bitcoin) - varies

### Processes
* M = Manual with UI
* S = System
* N = Notification in UI
* O = Outside System

#### Request BTC for fiat
1. (M) Alice: Select Amount in BTC (if multiple options)
2. (M) Alice->Peers: Request BTC for fiat
3. (M) Bob: Select Amount in BTC (if multiple options)
4. (M) Bob->Peers: Request fiat for BTC
5. (S) Bob: Match Alice's Request to Bob's Request
6. (S) Bob: Lock Bob's Request
7. (S) Bob->Alice: Accept Alice's Request
    1. (S) Alice->Bob: Alice's Request already locked by another request (alternative)
    2. (S) Bob: Unlock Bob's Request
    3. (S) Bob: Return to 5 until another match discovered
8. (S) Alice: Lock Alice's Request
9. (S) Alice/Bob->BTC: Establish Escrow
10. (N) Bob/Alice: Request Accepted (Escrow Established Notification with Terms)
11. (O) Bob satisfies terms (sends fiat)
12. (M) Bob->Alice: Terms Met
13. (O) Alice verifies terms met by Bob
14. (M) Alice->Bob: Terms Met Acknowledged
15. (S) Alice/Bob->BTC: Close Escrow
16. (S) Bob->Peers: Bob's Request Closed
17. (S) Bob->Alice: Escrow Closed
18. (S) Alice->Peers: Alice's Request Closed

## Build
On Linux:
* Requires OpenFX 11 included in libs directory.
* Requires Oracle JDK 11 for creating deb and rpm installers.
* Uses OpenJDK 11 for JRE.


