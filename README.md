# Raft-Based Distributed Lock Manager (Java, Spring Boot)

This project implements a **Raft-inspired consensus system** to perform **leader election** and **distributed locking** across multiple nodes using **TCP-based peer-to-peer communication**.

The system runs without any frontend and is designed to demonstrate **core distributed systems concepts**.

---

## 🔹 Why this project?

Most backend systems rely on tools like **ZooKeeper, etcd, or Redis** for leader election and distributed locks.

This project reimplements those ideas from scratch to show:
- Understanding of **Raft consensus**
- Handling of **node failures**
- Coordination in **distributed systems**
- Low-level networking using **TCP**

---

## 🔹 Core Concepts Implemented

### ✔ Raft Leader Election
- Nodes start as `FOLLOWER`
- Randomized election timeouts
- Vote-based leader election
- Split-vote recovery
- Automatic leader failover

### ✔ Heartbeat Mechanism
- Leader sends periodic heartbeats
- Followers reset election timeout
- Leader failure detected via heartbeat absence

### ✔ TCP-Based Peer Communication
- Nodes communicate using raw TCP sockets
- Custom RaftMessage protocol
- Separate ports for HTTP and Raft traffic

### ✔ Distributed Lock Manager (Leader-only)
- Only leader grants locks
- Lock TTL support
- In-memory lock tracking

---

## 🔹 Architecture Overview

```bash

+-----------+ TCP +-----------+
| Node A | <--------------> | Node B |
| (Leader) | | (Follower)|
+-----------+ +-----------+
^
|
v
+-----------+
| Node C |
| (Follower)|
+-----------+

```
- **HTTP Port** → Monitoring / status
- **TCP Port** → Raft consensus messages

---

## 🔹 Tech Stack

- Java 17+
- Spring Boot
- Virtual Threads
- TCP Sockets
- No external coordination services

---

## 🔹 How to Run (3 Nodes)

### Terminal 1
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=node1
```
### Terminal 2
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=node2
```
### Terminal 3
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=node3
```
🔹 Verify Leader Election

Check cluster status:
```bash
GET http://localhost:8081/cluster/status
GET http://localhost:8082/cluster/status
GET http://localhost:8083/cluster/status
```

One node will report:
```bash
{
  "state": "LEADER",
  "term": 1
}
```

🔹 Failure Handling

- Stop the leader node

- Remaining nodes automatically start a new election

- New leader is elected without downtime

🔹 What’s Not Implemented (By Design)

- Log replication

- Persistent storage

- Snapshotting

- Frontend UI

This keeps the focus on core consensus logic, not boilerplate.

🔹 What This Demonstrates to Interviewers

- Deep understanding of distributed systems

- Ability to reason about race conditions

- Comfort with low-level networking

- Production-style thinking

🔹 Future Improvements

- Persistent Raft log

- REST-based lock API

- Client-side retries

- Network partition simulation

## 🔹 Optional Frontend (Cluster Visualization)

A minimal frontend is included only to visualize cluster state.

The frontend:
- Polls `/cluster/status` from each node
- Displays node role (Leader / Follower)
- Shows current term and leader ID

⚠️ The frontend does **not participate** in consensus and has **no impact** on correctness.
All Raft logic runs entirely on the backend.

