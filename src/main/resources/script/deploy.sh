aym_api_id=`ps -ef | grep cactus | grep -v "grep" | awk '{print $2}'`
echo $aym_api_id

kill -9 $aym_api_id
echo "killed $aym_api_id"

sleep 2

echo "wait for aym_api_id server start............."

nohup java -jar cactus.jar  >> log.file 2>&1 &