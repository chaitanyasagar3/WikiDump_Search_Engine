import re
from lxml import etree 
import sys



i=1
count=0
j=0
entries = []
references = []
main=0
q = open("big_sample/references.txt", "w")
print "***Log Info: Started file parse."

for _, element in etree.iterparse(sys.argv[1], tag='{http://www.mediawiki.org/xml/export-0.8/}page'):
	entry = ""

	title = element.findtext('.//{http://www.mediawiki.org/xml/export-0.8/}title')
	ns = element.findtext('.//{http://www.mediawiki.org/xml/export-0.8/}ns')
	if ns == "0":
		body = element.findtext('.//{http://www.mediawiki.org/xml/export-0.8/}text')
		if "#redirect" not in body.lower():
			
			ref_num = re.subn("<ref", '', body)[1]
			page_entry = "<$page>\n<$id>" + str(count) + "</$id><$title>" + title.encode('utf-8') + "</$title>\n<$text>" + body.encode('utf-8') + "</$text>\n</$page>"
			page_entry = page_entry + "\n"
			entries.append(page_entry)
			reference_count = str(count) + ', ' + str(ref_num) + '\n'
			count+=1
			q.write(reference_count)
			
		if count % 500000 == 0:
			print "***Log Info: Writing to file output" + str(i) + ".txt"
			f = open("big_sample/output" + str(i) + ".txt", 'w')
			for entry in entries:
				f.write(entry)
			i +=1
			f.close()
			entries = []
		j+=1
	main+=1	
	element.clear()

print "***Log Info: Writing remaining entries to file."

f = open("big_sample/output" + str(i) + ".txt", 'w')
for entry in entries:
	f.write(entry)
f.close()
q.close()

print "***Log Info: Completed parsing."		
print "***Total number of pages after removing redirect: " + str(count)
print "***Total number of pages: " + str(main)
print "***Total files with ns=0: " +str(j)

