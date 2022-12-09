import operator

ref = {}
with open("big_sample/references.txt", "r") as infile:
	for line in infile:
		line = line.rstrip("\n")
		pair = line.split(", ")
		ref[int(pair[0])] = int(pair[1])



sorted_ref = sorted(ref.iteritems(), key=operator.itemgetter(1))
sorted_ref.reverse()
f = open('sorted.txt', 'w')
for i in sorted_ref:
  	f.write(' '.join(str(s) for s in i) + '\n')
f.close()
