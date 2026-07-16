# KanilaApp Model Audit Report

## 1. Executive Summary
This report summarizes the introspection of the KanilaApp MongoDB Mongoose models.

## 2. Summary Stats
- Total Model Files Scanned: 81
- Models Successfully Analyzed (Runtime): 80
- Models Analyzed via Static Fallback: 1
- Total Models Discovered: 81
- Total Fields Discovered: 1051
- Total Relationships: 136
- Total Indexes: 168
- Total Issues Identified: 5

## 3. Issues by Severity
- [HIGH] AuthOtp.otp_hash: Sensitive field without select: false
- [HIGH] AuthOtp.magic_token_hash: Sensitive field without select: false
- [MEDIUM] cartSummary.model.: Model file could not be required in isolation
- [HIGH] EmailOtp.otp_code_hash: Sensitive field without select: false
- [HIGH] EmailOtp.otp_code_hash: Sensitive field without select: false

## 4. Usage Instructions
- **ERD Drawing**: Import `KANILA_ERD.dbml` into [dbdiagram.io](https://dbdiagram.io).
- **Mermaid Graph**: Copy `KANILA_ERD.mmd` into any Markdown viewer that supports Mermaid or [mermaid.live](https://mermaid.live).
- **Detailed Schema**: View `KANILA_MODEL_RELATIONSHIP_MASTER.xlsx`.