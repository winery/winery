import React from 'react'
import PropTypes from 'prop-types'
import styled, { withTheme } from 'styled-components'
import { fontSize, space, color } from 'styled-system'

const css = key => props => props.theme[key]

const Image = styled.img([], {
  padding: '-3em'
})
