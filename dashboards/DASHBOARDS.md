# Metrics Dashboards
Once your service is in AWS it's good to know how it's doing by looking at [Atlas](http://go/atlas) Dashboards.
To create and publish your dashboard to Atlas we use [Hyperion](http://go/hyperion) which is a library written in Clojure.

You will notice a `project.clj` instead of a `build.gradle` file.
If you're not familiar with Clojure and it's build tool, [Leiningen](http://leiningen.org/) 
this is what `project.clj` is for. We rely on Leiningen to build and run the code that updates our live Dashboards.

## How to edit your Dashboards
The `master.clj` file composes the final dashboard. If you need to change the layout of your
final dashboard, this is the place. Do not use this file to explicitly add modules or metrics.
It's best to create a new `.clj` file and link the dashboard into master the same way
the `sunjeetsonboardingroot.clj` Dashboard is created.

The `sunjeetsonboardingroot.clj` file contains all your business logic metrics.
Every time you add a custom metric to your service be sure to include it here. Do not
use this file to add metrics or modules that are not defined in your business logic
(e.g., HTTP, system metrics, etc.). Add those to a separate `.clj` file.

Any metrics, modules and dashboards that are generic to services can be found inside
the hyperion project itself here:

- [hyperion.metrics](https://stash.corp.netflix.com/projects/WEDGE/repos/hyperion/browse/src/main/clojure/hyperion/metrics.clj)
- [hyperion.modules](https://stash.corp.netflix.com/projects/WEDGE/repos/hyperion/browse/src/main/clojure/hyperion/modules.clj)
- [hyperion.dashboards](https://stash.corp.netflix.com/projects/WEDGE/repos/hyperion/browse/src/main/clojure/hyperion/dashboards.clj)

To see how to define your own metrics, modules, dashboards look at the
[Hyperion custom module example](https://stash.corp.netflix.com/projects/WEDGE/repos/hyperion/browse/src/main/clojure/hyperion/sample/custom_modules.clj)

If you think that a metric that you need is not there be sure to send a note to the
[Java Generators User GG](https://groups.google.com/a/netflix.com/forum/?hl=en#!forum/java-generator-users)
 as this might be interesting to others and we can add it to Hyperion or the generator.
 
## How to upload your updated Dashboards
Once you upload your dashboards you can see them at:

http://dashboards.prod.netflix.net/show/sunjeetsonboardingroot

### Manually
Install `lein` to your machine. You only need to do that step once.
```
curl 'https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein' > /usr/local/lein
chmod a+x /usr/local/lein
```

To update simply run (from within the `dashboards/` directory):
```
/usr/local/lein run sunjeetsonboardingroot.dashboards.master/update
```

### Jenkins
The generator has created a Jenkins job that will update your Dashboards every time you push
code to your master branch. Check your README.md file for a link to the "Update Metrics Dashboards" job.
