Funny thing about this assignment is that I finally got solved when read somewhere (not at Coursera)
about A*algorithm.
At Coursera assignment description you may find:
"A critical optimization. Best-first search has one annoying feature: search nodes corresponding 
to the same board are enqueued on the priority queue many times. 
To reduce unnecessary exploration of useless search nodes, when considering the neighbors of a search node, 
don't enqueue a neighbor if its board is the same as the board of the previous search node."
Well, that information is partially correct, because you must check not only "board of the previous search node"
but also other "boardS of the previous search nodeS", that is a great difference, and firstly I thought other
search nodes may not be checked because of some A*algorithms mathematical aspects, which let us to reduce checks.
But thats not true, we have to check all the previously searched boards, or we can't guarantee critical optimization
and, of course, the solution.

# Algs-8-puzzle
A couple of assignments from Coursera Algorithms Part I course.
