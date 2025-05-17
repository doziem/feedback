# User Feedback Service

A Spring Boot application that allows users to submit feedback and admins to view filtered feedback submissions.

## Features

- Submit feedback with message and rating (1-5)
- Admin view of all feedback submissions
- Filter feedback by rating
- Secure admin endpoints with role-based auth
- Automatic timestamping of submissions

## API Documentation

### 1. Submit Feedback

**Endpoint**: `POST /api/feedback`

**Request**:
```json```
{
  "userId": "user123",
  "message": "Superb service!",
  "rating": 5
}

**Response (201 Created):**
```json```

{
  "id": "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8",
  "userId": "user123",
  "message": "Superb service!",
  "rating": 5,
  "createdAt": "2023-07-25T14:30:45.12345"
}

### Get All Feedback (Admin Only)

**Endpoint**: GET /api/admin/feedback

**Response (200 OK):**
```json```
[
{
"id": a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8,
"userId": "user123",
"message": "Great service!",
"rating": 5,
"createdAt": "2025-05-17T14:30:45.12345"
},
{
"id": b2a1c3d4-e5f6-7890-g1h2-i3j4k5l6n8m7,
"userId": "user456",
"message": "Could be better",
"rating": 3,
"createdAt": "2025-05-17T15:45:22.98765"
}
]

### Bonus (Optional) 
### 3. Filter Feedback by Rating (Admin Only)

**Endpoint**: GET /api/admin/feedback/by-rating/{rating}

**Example**: GET /api/admin/feedback/by-rating/5

**Response (200 OK):**
```json```
[
{
"id": 1,
"userId": "user123",
"message": "Great service!",
"rating": 5,
"createdAt": "2023-07-25T14:30:45.12345"
}
]