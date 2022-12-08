#!/usr/bin/env sh
case $LOG_VERBOSE in
'false')
	if [ $1 = 'stdin' ]; then
		0>/dev/null
	else
		$@ 1>/dev/null
	fi
	;;
*)
	if [ $1 = 'stdin' ]; then
		cat <&0
	else
		$@
	fi
	;;
esac
