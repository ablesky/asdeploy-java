echo 'This is a deploy process simulation for develop environment'
echo "The input arguments are: "
echo "    $@"
echo ""
echo '[ deploy  start ]'
for((i=1; i<=5; i++)); do
	echo "[ -- step  $i -- ]"
	sleep 2
done;
echo '[ deploy finish ]'
echo ""