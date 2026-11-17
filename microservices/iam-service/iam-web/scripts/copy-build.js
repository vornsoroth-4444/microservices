const fs = require('fs')
const path = require('path')

const src = path.join(__dirname, '..', 'build-web')
const dest = path.join(__dirname, '..', '..', 'iam-main', 'src', 'main', 'resources', 'static')

if (!fs.existsSync(dest)) {
  fs.mkdirSync(dest, { recursive: true })
}
fs.cpSync(src, dest, { recursive: true })
console.log(`Successfully synced build-web -> ${dest}`)
