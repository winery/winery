
# Topology Modeler Code Presentation

This presentation can be useful for new developers that want to dive into the code of the topology modeler.

## Development

Don't forget:
```sh
npm install
```

To run the presentation deck in development mode:

```sh
npm start
```

Edit the [`deck.mdx`](deck.mdx) file to edit the slides.

## Exporting

To build the presentation deck as static HTML:

```sh
npm run build
```

To export a PDF:

```sh
npm run pdf
```

To export an image of the title slide:

```sh
npm run image
```

## Important Information

All build commands add the generated files to the `/dist` folder.

As of 31. October, 2018 the `npm run pdf` command does not seem to be able to render
the animation steps of the code-navigation library `mdx-deck-code-surfer` used in these slides.
Therefore it is highly recommended to use the `npm run build` command for now and view the static HTML version
of the presentation in a browser until this issue is resolved.
You can follow this issue [here](https://github.com/jxnblk/mdx-deck/issues/211).

For more documentation see the [mdx-deck][] repo.

[mdx-deck]: https://github.com/jxnblk/mdx-deck
