### Box for all things that you are able to imagine.

* **EverboxingModule** ~ base implementation (some kinda _skeleton_)
    * **Checkstyle**: ./gradlew checkstyle
    * **Lint**: ./gradlew lint
    * **Findbugs**: ./gradlew findbugs
    * **Pmd**: ./gradlew pmd
    * **Analyze code (± lint, findbugs, pmd, checkstyle)**: ./gradlew analyzecode
    * **Build**: ./gradlew clean assembleDebug{Release}
    * **Tests**: ./gradlew test
    * **Show dependencies for configuration**: ./gradlew -q dependencies Everboxing:dependencies --configuration testCompile
