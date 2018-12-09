import theme from 'mdx-deck/themes'
import Image from './Image'

export default {
  ...theme,
  // add a custom font
  font: 'Roboto, sans-serif',
  // custom colors
  colors: {
    text: '#fff',
    background: '#242442',
    link: '#faf'
  },
  components: {
    img: Image
  }

  // Customize your presentation theme here.
  //
  // Read the docs for more info:
  // https://github.com/jxnblk/mdx-deck/blob/master/docs/theming.md
  // https://github.com/jxnblk/mdx-deck/blob/master/docs/themes.md
}
