### Box for all things that you are able to imagine.

**Contains the following apps**:
   * <img src="https://lh3.googleusercontent.com/2BA1Fnh-klEBv52QAJlx8_u8-93XlqTTldQrCIstz7-Nx_7cXGY9csROyDvj9OvSx_J1=w300-rw" width="30" height="30"> [LexisPaper on Google Play](https://play.google.com/store/apps/details?id=org.brainail.EverboxingLexis "LexisPaper on Google Play") - *Simple dictionary and offline Wiki style articles reader - refined version of Aard2*
<p align="center">
  <img src="https://lh3.googleusercontent.com/_xzA6hNdo3-JNB1p7GinCHi6ZsU2-fnmXTjH8PXIb1qmVXqVT4m7r1WPOK9cIdut8-VJ=h900-rw" width="256"/>
  <img src="https://lh3.googleusercontent.com/_SeWOnmVqjLFSlPDno8bD5Ry4qabjhvCYo_pSQFFpbxIvQ8RwBUsjAwpKANc9FbYh7w=h900-rw" width="256"/>
  <img src="https://lh3.googleusercontent.com/iQ4xQGYgpvwVErSnEllQo3cQcHXtwHshrosXueR4COl1SjVZkfbhzZc-7rrZDBmApw=h900-rw" width="256"/>
</p>

   * <img src="https://lh3.googleusercontent.com/wcrOuiCKAGbfWd34SdvbOJE5fg_s_GIIdkrnC5PnwioZbWX_MxTzGVZ1281_PZLbFaxZ=w300-rw" width="30" height="30"> [SplashFlame on Google Play](https://play.google.com/store/apps/details?id=org.brainail.EverboxingSplashFlame "SplashFlame on Google Play") - *Explore images by the fractal flame algorithm!*
   <p align="center">
  <img src="https://lh3.googleusercontent.com/zypzp0OVLv-B9GfmF_GKacWazRwghvV4edE-Mf8IH4kz-Cq6VbU0_hpm1jBrSr_ssw=h900-rw" width="256"/>
  <img src="https://lh3.googleusercontent.com/8E5CIoqm0qRmxDvWdkhGCV-36vp5LRmsWUDw5YxFFCy6osZmuRctNJgODUwGoKGC8Uk=h900-rw" width="256"/>
  <img src="https://lh3.googleusercontent.com/p0J3TJqUuol3tKgMrKII0fQGYQqbrq3uEABuXWAWCK2z5BmdmxkXcBYk2HbxIZyBOQ=h900-rw" width="256"/>
   </p>

## **Everboxing{Module}** ~ base implementation (some kinda _skeleton_):
* **Checkstyle**: ./gradlew checkstyle
* **Lint**: ./gradlew lint
* **Findbugs**: ./gradlew findbugs
* **Pmd**: ./gradlew pmd
* **Analyze code (± lint, findbugs, pmd, checkstyle)**: ./gradlew analyzecode
* **Build**: ./gradlew clean assembleDebug{Release}
* **Tests**: ./gradlew test
* **Show dependencies for configuration**: ./gradlew -q dependencies Everboxing{Module}:dependencies --configuration testCompile
