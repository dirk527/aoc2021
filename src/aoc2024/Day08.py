import itertools

file = open('08-in.txt', 'r')

towers = dict()
row = 0
for line in file:
	line = line.strip()
	col = 0
	for sym in line:
		if sym != '.':
			if not sym in towers:
				towers[sym] = list()
			towers[sym].append((row, col))
		col += 1
	row += 1

print(towers)
antinodes = set()
print("rows " + str(row) + " cols " + str(col))
for letter in list(towers):
	print(f"starting with {letter}")
	for (t1, t2) in itertools.combinations(towers[letter], 2):
		(r1, c1) = t1
		(r2, c2) = t2
		cand1 = (r1 - (r2 - r1), c1 - (c2 - c1))
		cand2 = (r2 - (r1 - r2), c2 - (c1 - c2))
		print(f"{t1} and {t2} -> {cand1} and {cand2}")
		antinodes.add(cand1)
		antinodes.add(cand2)

within = [ (r, c) for (r, c) in antinodes if r>=0 and r<row and c>=0 and c<col ]
print(f"p1: {len(within)}")

antinodes = set()
for letter in list(towers):
	for (t1, t2) in itertools.combinations(towers[letter], 2):
		(r1, c1) = t1
		(r2, c2) = t2
		
		dr = r2 - r1
		dc = c2 - c1
		r = r1
		c = c1
		while r >= 0 and r < row and c >= 0 and c < col:
			antinodes.add((r, c))
			r += dr
			c += dc
		dr = r1 - r2
		dc = c1 - c2
		r = r2
		c = c2
		while r >= 0 and r < row and c >= 0 and c < col:
			antinodes.add((r, c))
			r += dr
			c += dc	  
print(f"p2: {len(antinodes)}")
