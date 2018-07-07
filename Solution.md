## Facts
- the coordinates are WSG84, this projection is not based on euclidean distance, thus we cannot use techniques that rely on this metric
- the number of airports is small and finite compared to the location reports
- because of that it does not seem useful to shard the airport across the keyspace
- instead, data is made available on any cluster
- to improve access latency we prefer memory access and try to avoid accessing some remote machine
- the naive approach has complexity n, i.e. for every location report we would have to scan all airports to find the closest match
- thus, we build a  Balltree [1] in advance. This tree allows access on average in log(n).
- the Balltree (compared to a K-D-tree) can use arbitrary distance metrics (such as haversine)

## Benchmark 
- the application has been run on a Lenovo Thinkpad T440s with 12 GB of RAM and a Intel(R) Core(TM) i5-4300U
- the naive implementation needed almost 50 minutes to execute
- the improved Balltree version needed a bit less than two minutes


 [1] https://github.com/EdwardRaff/JSAT/blob/master/JSAT/src/jsat/linear/vectorcollection/BallTree.java
 
 
## Run Application
- The application is setup to run completely embedded (no submission to a cluster required)
- Check out with your IDE, install dependencies 
- ADOPT Params.scala to point to your the input files
- run the main class NearestAirportApplication
- A simple unit test has been implemented for both the naive and tree-based approach
