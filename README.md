# ClayJ
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.christechs/ClayJ)

ClayJ is a zero external dependency, UI layout library for Java.

It is a pure Java port of [Clay](https://github.com/nicbarker/clay), designed to be framework and backend agnostic.

## Note
**This project made extensive use of AI to assist in translating layout logic
from the original Clay C project.**

## Acknowledgments & Credits

This project would not exist without the work of the following developers:
* **[Nic Barker (nicbarker)](https://github.com/nicbarker)** - The creator of the original **[Clay](https://github.com/nicbarker/clay)** and the contributors of **[Clay](https://github.com/nicbarker/clay)**. ClayJ directly implements the layout logic of **[Clay](https://github.com/nicbarker/clay)**.
* **[Patricio Whittingslow (soypat)](https://github.com/soypat)** - The author of **[Glay](https://github.com/soypat/glay)**, a Go port of Clay.

## Installation

ClayJ is available on **Maven Central**.

### Maven

```xml
<dependency>
    <groupId>io.github.christechs</groupId>
    <artifactId>ClayJ</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Gradle (Groovy)

```groovy
implementation 'io.github.christechs:ClayJ:1.1.0'
```

### Gradle (Kotlin)

```kotlin
implementation("io.github.christechs:ClayJ:1.1.0")
```

## Quick Start

### Initialization and Setup
```java
import io.github.christechs.clayj.ClayJContext;
import io.github.christechs.clayj.math.Dimensions;

ClayJContext context = ClayJContext.createDynamic();

context.errorHandler = errorType -> {
    System.err.println("ClayJ Error [" + errorType + "]: " + errorType.getDefaultMessage());
};

context.measureTextFunction = (text, start, len, config) -> {
    float width = MyRenderer.measureText(text.subSequence(start, start + len), config.fontSize);
    float height = config.lineHeight > 0 ? config.lineHeight : config.fontSize;
    return new Dimensions(width, height);
};
```

### Layout Loop

```java
import io.github.christechs.clayj.enums.*;
import io.github.christechs.clayj.LayoutResults;
import io.github.christechs.clayj.math.Vector2;
import static io.github.christechs.clayj.ClayJ.*;

public void render(float deltaTime) {

   runLayout(context, () -> {
      setLayoutDimensions(windowWidth, windowHeight);

      setPointerState(new Vector2(mouseX, mouseY), isMouseDown);
      updateScrollContainers(true, new Vector2(scrollDeltaX, scrollDeltaY), deltaTime);

      beginLayout();

      el(decl().id("Root").bg(15, 15, 18)
              .layout(layout().widthGrow().heightGrow()
                      .dirTopToBottom()
                      .padding(16).gap(8)), () -> {

         el(decl().id("Header").bg(38, 38, 45)
                 .layout(layout().widthGrow().heightFixed(50)
                         .alignCenter()), () -> {

            text("Welcome to ClayJ", txt().size(24).color(245, 245, 250));
         });

         boolean isHovered = pointerOver("MyButton");
         el(decl().id("MyButton").bg(isHovered ? 130 : 100, 140, 255)
                 .layout(layout().widthFit().heightFixed(40)
                         .padding(16, 8)), () -> {

            text("Click Me!", txt().size(16).color(255, 255, 255));
         });

      });

      LayoutResults results = endLayout();

      renderUI(results);
   });
}
```

### Rendering the Results

```java
private void renderUI(LayoutResults results) {
   for (int i = 0; i < results.length(); i++) {
      RenderCommand cmd = results.get(i);

      switch (cmd.commandType) {
         case RECTANGLE:
            MyRenderer.drawRect(cmd.boundingBox, cmd.renderData.backgroundColor, cmd.renderData.cornerRadius);
            break;
         case TEXT:
            MyRenderer.drawText(cmd.renderData.text, cmd.boundingBox.x(), cmd.boundingBox.y(), cmd.renderData.textColor);
            break;
         case SCISSOR_START:
            MyRenderer.pushClip(cmd.boundingBox);
            break;
         case SCISSOR_END:
            MyRenderer.popClip();
            break;
         // Handle IMAGE, BORDER, and CUSTOM
      }
   }
}

```

---

## License

The original codebase and new contributions to this project are released into the public domain under The Unlicense.

However, this project heavily utilizes and references third-party software (Clay and Glay). The public domain dedication does not override their original licenses. Anyone distributing this project must still comply with the zlib and BSD 3-Clause licenses for those respective portions, which are detailed at the bottom of this file.

```text
This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <https://unlicense.org/>

```

### Third-Party Licenses

The projects below are heavily referenced or utilized within this software.
Their original copyright notices and licenses apply to their respective
portions of the code:

**Clay** (Original C Library)

zlib/libpng license

Copyright (c) 2024 Nic Barker

This software is provided 'as-is', without any express or implied warranty.
In no event will the authors be held liable for any damages arising from the
use of this software.

Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:

    1. The origin of this software must not be misrepresented; you must not
    claim that you wrote the original software. If you use this software in a
    product, an acknowledgment in the product documentation would be
    appreciated but is not required.

    2. Altered source versions must be plainly marked as such, and must not
    be misrepresented as being the original software.

    3. This notice may not be removed or altered from any source
    distribution.

**Glay** (Go Port)

BSD 3-Clause License

Copyright (c) 2023, Patricio Whittingslow

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
