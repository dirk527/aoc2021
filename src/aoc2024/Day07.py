def check(wish, cur, elements, index, concat):
#	print ("check " + str(wish) + ", " + str(cur) + " idx " + str(index))
	if cur > wish:
		return False
	if index == len(elements):
	  return cur == wish
	mitplus = check(wish, cur + elements[index], elements, index + 1, concat)
	if mitplus:
	  return True
	mitmal = check(wish, cur * elements[index], elements, index + 1, concat)
	if concat:
		if mitmal:
			return True
		return check(wish, int(str(cur) + str(elements[index])), elements, index + 1, concat)
	else:
	  return mitmal
	
f = open("07-in.txt", "r")
lines = f.readlines()
p1 = 0
p2 = 0
for line in lines:
  line = line.strip('\n')
  parts = line.split(": ")
  wish = int(parts[0])
  elements = [ int(x) for x in parts[1].split(" ") ]
  if check(wish, elements[0], elements, 1, False):
    p1 += wish
  if check(wish, elements[0], elements, 1, True):
    p2 += wish
  	
print(p1)
print(p2)
