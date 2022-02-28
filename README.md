# Desktop

## Services

### Commons

#### Agora

#### Browser

#### Dashboard

#### DEX (BTC-USD)
Purely noncustodial (2-2 multi-sig) escrows where only the exchange
participants' keys are used. There is no dispute resolution.
A node with more than 10 successful exchanges will flag another
node as a disputed exchange upon an unsuccessful exchange
(BTC is stuck in escrow for a period of 10 days).
Upon 3 flags of a node, the system bars that node from the exchange.

##### Crypto Supported
Only Bitcoin (BTC) is supported.

Increments available are based on daily (UT) volume:

* 0/day: 0.01
* 30/day: 0.001, 0.01
* 60/day: 0.001, 0.01, 0.1
* 90/day: 0.001, 0.01, 0.1, 1

##### Fiat Supported
Only USD is supported using Zelle and Mail.

##### Fees
* DEX - 0.5%
* External networks (e.g. Bitcoin) - varies

##### Constraints
* Only 1 Request can be made per node, additional requests are rejected by the network.
* Requests can be removed only if not taken (locked).
* BTC for fiat requests, the Maker pays the DEX fee
* Fiat for BTC requests, the Taker pays the DEX fee

#### Processes
* M = Manual with UI
* S = System
* N = Notification in UI
* O = Outside System

##### Request BTC for fiat
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

#### Topics

### Community

#### Agora

#### Calendar

#### Dashboard

#### Search

#### Social

#### Wallet

### Personal

#### Agora

#### Blog

#### Calendar

#### Identities

#### Wallet
* Creates a local wallet in the local BTC core node.
* Multiple wallets supported.
* Can send/receive BTC using the local BTC core node.
* Can sweep private keys to a wallet.
* Can see personal transactions.
* Can move BTC to a cold wallet (generated PDF) saved
to a local flash drive.

## Build
On Linux:
* Requires OpenFX 11 included in libs directory.
* Requires Oracle JDK 11 for creating deb and rpm installers.
* Uses OpenJDK 11 for JRE.


