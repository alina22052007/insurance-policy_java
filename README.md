# Insurance Policy & Claims Management System
**Case Study 59 | Java Programming (Semester III - B.Tech CSE)**

---

## 📌 Project Overview
This project is an **Insurance Policy & Claims Management System** built with Java and standard Java Swing. It models diverse insurance products (Life, Health, and Vehicle policies), covers the complete policy-to-claim lifecycle, and calculates premiums dynamically based on policy categories and risk factors.

The codebase is optimized to **747 lines** in a single file (`InsuranceManagementSystem.java`), making it simple, humanized, and readable for college semester evaluations.

---

## 🛠️ Java Concepts Implemented

| Java Concept | Application in Code |
|---|---|
| **Classes & Objects** | `Customer`, `Policy`, `Claim`, `Premium` |
| **Constructors** | Parameterized constructors for complete object initialization |
| **Abstract Class** | `abstract class Policy` (base for `LifePolicy`, `HealthPolicy`, `VehiclePolicy`) |
| **Method Overriding** | `calculatePremium()` uniquely implemented in each policy subclass |
| **ArrayList** | `ArrayList<Policy> policyList` for primary storage and dynamic iteration |
| **LinkedList** | `LinkedList<Claim> claimHistory` to maintain chronological claim records |
| **HashMap** | `HashMap<String, Policy> policyMap` for O(1) fast lookup by Policy Number |
| **TreeMap** | `TreeMap<Double, List<Policy>> policiesByPremium` for natural sorting by premium |
| **CRUD Operations** | Complete Create, Read, Update, and Delete operations for policies and claims |
| **Searching** | Multi-attribute search (by Policy Number, Customer Name, or Policy Type) |
| **Sorting** | Custom Comparators to sort policies by premium (asc/desc) and claims by amount |
| **Swing GUI** | Standard, clean Swing components (`JFrame`, `JTabbedPane`, `JTable`, `JPanel`, `JOptionPane`) |
| **Exception Handling** | Custom exceptions: `InvalidPolicyException` and `InvalidClaimException` |
| **Validation** | Policy number uniqueness, phone (10 digits), email format, coverage limits, claim documents |

---

## 📂 Expected Modules Included

1. **Customer Registration**: Register new policyholders with validation for phone numbers and email addresses.
2. **Policy Issuance (CRUD)**: Issue new policies across Life, Health, and Vehicle categories with dynamic form inputs and full update/delete capabilities.
3. **Premium Calculation**: Live preview and calculation of annual premiums using overridden formulas.
4. **Claim Processing**: File claims against active policies with coverage limit checks and approval/rejection workflows in a `LinkedList`.
5. **Payment Tracking**: Record and log premium payments with various payment methods.
6. **Policy Search & Sorting**: Search and filter policies by multiple parameters and sort by premium/duration.
7. **Claims Reports & Analytics**: Real-time metric cards, `TreeMap` sorted visualization, and claims sorting by amount.

---

## 🚀 How to Compile & Run

### 1. Open Terminal in the project folder:
```bash
cd "/Users/alinamukhtarpatel/Desktop/java major project"
```

### 2. Compile:
```bash
javac InsuranceManagementSystem.java
```

### 3. Run:
```bash
java InsuranceManagementSystem
```
