-- 2026/1/10
## What
Add async refresh mechanism for cached GitHub repositories

## Why
Cached repository data may become stale if GitHub data changes

## How
- Add TTL-based expiration
- Return stale data while refreshing asynchronously
- Persist cache history on data changes

## Design Goal Review
- Cache exists & not expired → Return directly
- Cache exists & expired → Return old data + background refresh
- Cache does not exist → Synchronously request GitHub → Persist to database → Return
- Concurrency safety, prevent cache breakdown

## Tests
- Added test to ensure expired cache returns old data
  
## Future extension
- Application events are used to keep the cache refresh logic simple 
  while allowing future extensions(history, metrics, notifications) 
  to be added in a decoupled way.