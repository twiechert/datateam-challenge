## Facts
- the coordinates are WSG84, this projection is not based on euclidean distance
- the number of airports is small and finite compared to the location reports
- because of that it does not seem useful to shard the airport across the keyspace
- instead, data is made available on any cluster
- to improve access latency we prefer memory access and try to avoid accessing some remote machine
- the naive approach has complexity n, i.e. for every location report we would have to scan all airports to find the closest match
- we build a Kd tree in advance. This tree actually expects euclidean distances. Thus, the results tend to be biased around the poles
- Better would be to build a Balltree that can also use other distance metrics (such as haversine)

## Benchmark 


## Futer Work
- if approximation is fine, shard also the aiport data across cluster such that airports close to each other
tend be on the same node as well
- then in the first phase, process an approximation to anwser in which grill cell the user is located at
- route then to the respective node (by keying by the grid cell identifier)