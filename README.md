

https://nodejs.org/download/release/v13.6.0/

----

https://nodejs.org/download/release/v14.0.0/

14.0.0 is/was the last varsion that could do Windows 7

> https://nodejs.org/download/release/v14.0.0/node-v14.0.0-win-x64.zip

LIES

-------

------
# puresand (WIP)

radical way to resolve my problems with NodeJS/npm/Spago by building a whole environment of my very own

## experimentation

i downloaded the/a node archive and it seem good!

... but ...

envornment variables under windows seem weird to me.

the vars seem to lose their values and then the/a bat files stop working?

```bat
@ECHO OFF

ECHO setting up yer fake nodeless environment

SET NODE_SKIP_PLATFORM_CHECK=1

SET PATH=C:\Users\Peter\opt\cmder\vendor\bin;C:\Users\Peter\opt\PortableGit\cmd;C:\Users\Peter\opt\cmder\vendor\conemu-maximus5\ConEmu\Scripts;C:\Users\Peter\opt\cmder\vendor\conemu-maximus5;C:\Users\Peter\opt\cmder\vendor\conemu-maximus5\ConEmu;C:\Users\Peter\opt\AdoptOpenJDK\jdk-11.0.8.10-hotspot\bin;C:\opt\sox-14.4.2;C:\opt\PortableGit-2.24.1.2-64-bit\bin;C:\Program Files (x86)\Common Files\Microsoft Shared\Windows Live;C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v7.5\bin;C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v7.5\libnvvp;C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v9.1\bin;C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v9.1\libnvvp;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\ProgramData\chocolatey\bin;C:\Program Files\PuTTY\;C:\Program Files (x86)\GtkSharp\2.12\bin;C:\Program Files\dotnet\;C:\Program Files\Java\jdk1.8.0_162\bin;C:\Users\Peter\.dnx\bin;C:\Program Files\Microsoft DNX\Dnvm\;C:\Program Files\Microsoft SQL Server\130\Tools\Binn\;C:\Program Files\Microsoft SQL Server\110\Tools\Binn\;C:\Program Files\Git LFS;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\Program Files\TortoiseHg\;C:\Program Files (x86)\HP\Common\HPDestPlgIn\;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Users\Peter\opt\PortableGit\bin;C:\Users\Peter\opt\PortableGit\bin;C:\Users\Peter\opt\bin;C:/Users/Peter/AppData/Local/Android/Sdk/tools;C:/Users/Peter/AppData/Local/Android/Sdk/platform-tools;C:\JavaSDK\bin\client;C:\JavaSDK\bin;C:\Users\Peter\AppData\Local\Programs\Python\Python38\Scripts\;C:\Users\Peter\AppData\Local\Programs\Python\Python38\;C:\Users\Peter\opt\swigwin-4.0.1;C:\Users\Peter\opt\Python-3.8.3\Scripts\;C:\Users\Peter\opt\Python-3.8.3\;C:\Users\Peter\opt\pandoc-2.9.1.1-windows-x86_64;C:\Users\Peter\opt\cmake-3.13.4-win32-x86/bin;C:\Users\Peter/opt/ffmpeg-4.1-win64-static/bin;C:\Users\Peter/opt/ammonite;C:\Users\Peter/opt/bin;C:\Program Files\Common Files\Microsoft Shared\Windows Live;C:/tools/mingw64/bin;C:\Users\Peter\.dnx\bin;C:\Users\Peter\opt\cmder;C:/Program Files/CMake/bin;C:\Program Files\Microsoft VS Code\bin;C:\tools\mingw64\bin;C:\Program Files\kdiff3;C:\Users\Peter\AppData\Local\atom\bin;C:\Users\Peter\AppData\Local\Programs\Microsoft VS Code\bin;C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2018.3.2\bin;C:\Users\Peter\opt\MiKTeX\miktex\bin\x64\;C:\Users\Peter\opt\cmake-3.19.1-win64-x64\bin;C:\Program Files (x86)\HP\Common\HPDestPlgIn\;C:\Users\Peter\AppData\Roaming\npm;C:\Users\Peter\opt\PortableGit\mingw64\bin;C:\Users\Peter\opt\PortableGit\usr\bin;

ECHO now adding your node

SET PATH=C:\Users\Peter\Desktop\node-v14.17.0-win-x64;%PATH%

ECHO update npm

CMD /C "npm update -g npm"

ECHO let's install spago

CMD /C "npm update -g spago@0.20.3 && which spago"

which spago

ECHO let's install spurescript

npm install -g purescript@0.14.2

which purs

```
