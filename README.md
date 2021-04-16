# EmoteVilleSpawner   

Plugin made for spigot 1.16.5


## Building  

### IntelliJ IDEA  
Import to IDEA, wait for it to build indexes and dependencies. If you encounter symbol errors, 
close and reopen the project.  

### NMS Dependency   
Run Spigot BuildTools, paste the file `Spigot/Spigot-Server/target/spigot-1.16.5-R0.1-SNAPSHOT.jar` in folder 
`libs`. 
Gradle will search for it.


### Generating jar  
run `./gradlew build`  
Your jars will be in the `build/lib` folder  
