# Desktop

## Services

### Commons
Open to everyone who is using 1M5.

#### Agora (future)
Open marketplace where participants are anonymous but items are not.

All participants must go through Non-Aggression Principle training
prior to being given access to the commons agora.

Item to sell must get approval from a high reputation judge that it
does not violate the Non-Aggression Principle (NAP).

If a judge denies the item claiming it violates the NAP, the participant
can elevate to a supreme judge.

If the supreme judge overrides the decision, the judge gets a reprimand.

After 3 reprimands, the judge is removed from being a judge.

If the supreme judge upholds the decision, the participant gets a private mark.

After 3 marks, the participant can't sell in the marketplace until they have gone through NAP training again.

After training, a mark is removed.

After 10 successful item sells, a mark gets removed.

Judges and supreme judges get judged too when they sell an item.

If a judge gets a mark, they lose the ability to judge until that mark is removed, either through training or by selling 10 items with no marks.

If a supreme judge gets a mark, they get demoted to judge.

If a judge elevates a decision to a founding judge and the decision is overturned, the supreme
judge gets a reprimand.

After 3 reprimands, the supreme judge is demoted to judge.

A supreme judge loses a reprimand after 10 successful elevated judgements.

If the founding judge upholds the decision, the judge gets a reprimand.

After 3 reprimands, the judge loses their ability to judge.

A judge loses a reprimand after 10 successful judgements.

Participants become judges when they have 100 successful trades and no marks.

Judges become supreme judges when they have 1000 successful judgements and no reprimands.

There are only up to four founding judges at any given time.

Founding judges get elected by current founding judges upon a vacancy requiring unanimity.

Fees are 0.5% per transaction.

50% of the fee goes to developers and 50% goes to judges.

Judges fees are broken down as follows: 33% spread among active judges, 33% spread evenly among active supreme judges, 33% spread among active founding judges.

Judges are considered active if they have judged at least one decision within the last 30 days.

Supreme judges are considered active if they have judged at least one decision within the last 126 days.

Founding judges are considered active if they have judged at least one decision within the last 252 days or have voted in a founding judge.

Founding judges can change the parameters on when a judge and supreme judge are chosen but must be unanimous.

#### Browser
Browses public sites using Tor. Will use I2P or other protocols to tunnel to Tor if/when needed.

#### Dashboard
Shows summary information on commons activities.

* Status of any open exchange request

#### DEX (BTC-USD)
Purely noncustodial (2-2 multi-sig) escrows where only the exchange
participants' keys are used. There is no dispute resolution.
A node with more than 10 successful exchanges will flag another
node as a disputed exchange upon an unsuccessful exchange
(BTC is stuck in escrow for a period of 10 days).
Upon 3 flags of a node by a reputable node, the network bars that node from the exchange.

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

#### Topics (future)
Can request information on topics - information comes in asynchronously over
time as it is found...like a decentralized RSS feed.

### Community

#### Dashboard (future)
Community Dashboard
* Shows Communities created (future)
* Shows Communities joined (future)

#### Search (future)
Search for Communities with similar interests
* Create Community (future) - creates a new tab with directions on setting up the community
* Request to join Community (future)

#### Community 1 (future)
Each Community gets a separate tab

##### Manage (future)
Provide community management tools
* Determine who can join the community (future)
* Remove community member (future)

##### Agora (future)
Community Shops (future) - Community runs the shops, e.g. intentional communities.

##### Calendar (future)
Community Calendar

##### Social
Communications between Community members and between communities.
* Send text to Contact

##### Wallet (future)
Community Wallet

Creates shared multi-sig wallets in the local BTC core node
distributing them to all participants.

* Multiple wallets supported (future)
* Create shared multi-wallet distributing to participants (future)
* One shared wallet automatically created for each Community (future)
* One shared wallet per Community Agora shop can be created (future)
* Can send/receive BTC using the local BTC core node.
* Can import private key to a wallet. (future)
* Can sweep private key to a wallet. (future)
* Can see community transactions.
* Can move BTC to a cold wallet (generated PDF) saved to a local flash drive. (future)

### Personal

#### Agora (future)
Personal Shops
* Determine who can see the shop (future)
* Manage Items (future)

#### Blog (future)
Personal Blogs
* Determine who can see the blog (future)
* Publishing lifecycle (future)
* Determine who can edit the blog (future)

#### Calendar (future)
Personal Calendar

#### Contacts
* Add Contact (Import) (future)
* Remove Contact (future)
* View Contact Detail (future)
* View Contacts list (future)

#### Identities
* Create Identity
* Add Identity (Import) (future)
* Remove Identity (future)
* Destroy Identity (future)
* View Identity detail (future)
* View Identity list (future)

#### Wallet
* Creates a local wallet in the local BTC core node.
* Multiple wallets supported.
* Can send/receive BTC using the local BTC core node.
* Can import private key to a wallet. (future)
* Can sweep private key to a wallet. (future)
* Can see personal transactions.
* Can move BTC to a cold wallet (generated PDF) saved to a local flash drive. (future)

## Build
On Linux:
* Requires OpenFX 11 included in libs directory.
* Requires Oracle JDK 11 for creating deb and rpm installers.
* Uses OpenJDK 11 for JRE.


