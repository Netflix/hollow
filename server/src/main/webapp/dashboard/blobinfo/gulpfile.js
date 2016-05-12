var gulp = require('gulp');
var connect = require('gulp-connect');
var modRewrite = require('connect-modrewrite');

gulp.task('connectDev', function () {
    connect.server({
        port: 3000,
        livereload: true,
        middleware: function (connect, opt) {
            return [
                modRewrite([
                  // Proxy all local calls to /REST/vms/* to the given instance on AWS
                  // Allows running the UI independently of the REST service and gets around CORS
                  '^/REST/vms/(.*)$ http://ec2-54-211-158-121.compute-1.amazonaws.com:7001/REST/vms/$1 [P]'
                ])
            ];
        }
    });
});

gulp.task('default', ['connectDev']);