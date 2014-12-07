@echo off
echo *** This is a deploy process simulation for develop environment ***
echo.
echo - The test script path is:
set "script_path=    %~f0"
echo %script_path%
echo.
echo - The input arguments are: 
set "args=    %*"
echo %args%
echo.
echo [ deploy  start ]
for /L %%i in (1,1,5) do (
	echo [ -- step  %%i -- ]
	call :sleep 3
)
echo [ deploy finish ]
goto :EOF
@echo on

:sleep
@echo off
ping -n %1 127.0.0.1 >nul
goto :EOF
