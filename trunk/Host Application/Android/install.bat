adb.exe connect 127.0.0.1:5555

adb.exe devices

echo adb.exe shell rm "/data/app/*"

adb.exe -s 127.0.0.1:5555 install -r "C:\Projects\Android\afarsek\bin\afarsek.apk"

adb.exe disconnect

adb.exe devices

pause