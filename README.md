-- 2026/1/10
## What
Add async refresh mechanism for cached GitHub repositories

## Why
Cached repository data may become stale if GitHub data changes

## How
- Add TTL-based expiration
- Return stale data while refreshing asynchronously
- Persist cache history on data changes

## Tests
- Added test to ensure expired cache returns old data