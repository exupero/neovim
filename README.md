# neovim

A data-driven API for writing Neovim plugins in Clojure.

## Usage

```
(require '[neovim.core :as n])

(def client (n/client 1 "localhost" 7777))

; Execute a single message, get back a single result
(n/exec client (n/command ":echo 'Hello, World'"))

; Execute multiple messages atomically, get all results back
(n/exec client [(n/eval "expand('%')") (n/eval "line('.')")])

; Execute messages that have messages nested inside them
(n/exec client (n/win-get-cursor (n/get-current-win)))

; Sequences will be flattened
(n/exec client [[(n/eval "expand('%')")] (n/eval "line('.')")])
```

Each Neovim API message has a corresponding function, where the function name is the same as the message name but without the prefix `nvim_` and with hyphens in place of underscores.

## To Do

- [ ] Structure return values to match the shape of the passed in messages (currently returns values based on the flattened structure)

## License

Copyright © 2017 Eric Shull

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
