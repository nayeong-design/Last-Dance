"""dist/ 빌드 결과를 아티팩트용 단일 HTML(+이미지 파일)로 묶는다."""
import glob, os, shutil, sys
out = sys.argv[1]
os.makedirs(out, exist_ok=True)
js = open(glob.glob('dist/bundle/*.js')[0]).read().replace('</script', '<\\/script')
css = open(glob.glob('dist/bundle/*.css')[0]).read()
html = f'''<title>Last Dance</title>
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Asta+Sans:wght@300..800&display=swap">
<style>{css}</style>
<div id="root"></div>
<script type="module">{js}</script>
'''
open(os.path.join(out, 'last-dance.html'), 'w').write(html)
if os.path.exists(os.path.join(out, 'assets')): shutil.rmtree(os.path.join(out, 'assets'))
shutil.copytree('dist/assets', os.path.join(out, 'assets'))
print(os.path.getsize(os.path.join(out, 'last-dance.html')))
