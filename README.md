# blind-app

A [re-frame](https://github.com/Day8/re-frame) application designed to take an
image as input and output a description of the image from Azure.

This was make very quickly for a friend to use to test out the technology for
use in his final year project, which was a pair of glasses that told blind users
what was in front of them.

To get this working you'll need to replace the string in the db.cljs file with
a valid azure key.

## Development Mode

### Start Cider from Emacs:

Put this in your Emacs config file:

```
(setq cider-cljs-lein-repl
	"(do (require 'figwheel-sidecar.repl-api)
         (figwheel-sidecar.repl-api/start-figwheel!)
         (figwheel-sidecar.repl-api/cljs-repl))")
```

Navigate to a clojurescript file and start a figwheel REPL with `cider-jack-in-clojurescript` or (`C-c M-J`)

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
