# Toy Giveaway Campaign - Implementation Plan

## Tổng Quan

Kế hoạch triển khai chức năng **Toy Giveaway Campaign** sử dụng lại entities hiện có (`Toy`, `Event`) thay vì tạo mới.

## Kiến Trúc Tổng Thể

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │   Application   │    │     Domain      │
│     Layer       │───▶│     Layer       │───▶│     Layer       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Infrastructure  │    │     Cache       │    │   Database      │
│     Layer       │    │    (Redis)      │    │ (PostgreSQL)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```
