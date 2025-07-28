"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { ArrowLeft, ArrowRight, Camera, Volume2, RotateCcw, Play } from "lucide-react"

type Screen = "splash" | "home" | "learn" | "ar" | "quiz" | "about"

export function SignLanguageApp() {
  const [currentScreen, setCurrentScreen] = useState<Screen>("splash")
  const [currentLetter, setCurrentLetter] = useState(0)

  const letters = [
    { letter: "A", description: "Jari telunjuk ke atas", gesture: "👆" },
    { letter: "B", description: "Telapak tangan terbuka", gesture: "✋" },
    { letter: "C", description: "Bentuk huruf C", gesture: "👌" },
    { letter: "D", description: "Jari telunjuk tegak", gesture: "☝️" },
    { letter: "E", description: "Kepalan tangan", gesture: "✊" },
  ]

  const SplashScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-cyan-200 to-cyan-300 flex flex-col items-center justify-center p-6">
      <div className="text-center space-y-8">
        <div className="text-8xl mb-4">🤟</div>
        <h1 className="text-4xl font-bold text-gray-800 mb-2">ISYARAT</h1>
        <h2 className="text-2xl font-bold text-gray-800">PINTAR</h2>
        <div className="w-16 h-1 bg-gray-600 mx-auto mt-8"></div>
      </div>
      <Button
        onClick={() => setCurrentScreen("home")}
        className="mt-12 bg-blue-500 hover:bg-blue-600 text-white px-8 py-3 rounded-full"
      >
        Mulai Belajar
      </Button>
    </div>
  )

  const HomeScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-yellow-200 to-yellow-300 p-6">
      <div className="max-w-md mx-auto">
        <div className="text-center mb-8">
          <h1 className="text-2xl font-bold text-gray-800 mb-2">Selamat Datang,</h1>
          <h2 className="text-xl font-semibold text-gray-700">Ayo Belajar Bahasa Isyarat!</h2>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <Card
            className="bg-blue-400 text-white cursor-pointer hover:bg-blue-500 transition-colors"
            onClick={() => setCurrentScreen("learn")}
          >
            <CardContent className="p-6 text-center">
              <div className="text-3xl mb-2">📚</div>
              <h3 className="font-semibold">Belajar Huruf</h3>
            </CardContent>
          </Card>

          <Card
            className="bg-red-400 text-white cursor-pointer hover:bg-red-500 transition-colors"
            onClick={() => setCurrentScreen("ar")}
          >
            <CardContent className="p-6 text-center">
              <div className="text-3xl mb-2">📱</div>
              <h3 className="font-semibold">AR Mode</h3>
            </CardContent>
          </Card>

          <Card
            className="bg-green-400 text-white cursor-pointer hover:bg-green-500 transition-colors"
            onClick={() => setCurrentScreen("quiz")}
          >
            <CardContent className="p-6 text-center">
              <div className="text-3xl mb-2">🧩</div>
              <h3 className="font-semibold">Kuis</h3>
            </CardContent>
          </Card>

          <Card
            className="bg-purple-400 text-white cursor-pointer hover:bg-purple-500 transition-colors"
            onClick={() => setCurrentScreen("about")}
          >
            <CardContent className="p-6 text-center">
              <div className="text-3xl mb-2">ℹ️</div>
              <h3 className="font-semibold">Tentang</h3>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )

  const LearnScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-cyan-200 to-cyan-300 p-6">
      <div className="max-w-md mx-auto">
        <div className="flex items-center mb-6">
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")} className="mr-4">
            <ArrowLeft className="h-6 w-6" />
          </Button>
          <h1 className="text-2xl font-bold text-gray-800">Belajar Huruf</h1>
        </div>

        <div className="flex justify-center mb-6 space-x-2">
          {letters.map((_, index) => (
            <Button
              key={index}
              variant={currentLetter === index ? "default" : "outline"}
              size="sm"
              onClick={() => setCurrentLetter(index)}
              className="w-10 h-10 rounded-full"
            >
              {letters[index].letter}
            </Button>
          ))}
        </div>

        <Card className="bg-white mb-6">
          <CardContent className="p-8 text-center">
            <div className="text-8xl mb-4">{letters[currentLetter].gesture}</div>
            <h2 className="text-3xl font-bold text-gray-800 mb-2">{letters[currentLetter].letter}</h2>
            <p className="text-gray-600 mb-4">
              {letters[currentLetter].letter} = {letters[currentLetter].description}
            </p>
            <Button className="bg-blue-500 hover:bg-blue-600 text-white px-8 py-2 rounded-full">
              <Volume2 className="h-4 w-4 mr-2" />
              Dengar Suara
            </Button>
          </CardContent>
        </Card>

        <div className="flex justify-between">
          <Button
            variant="outline"
            onClick={() => setCurrentLetter(Math.max(0, currentLetter - 1))}
            disabled={currentLetter === 0}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Sebelumnya
          </Button>
          <Button
            onClick={() => setCurrentLetter(Math.min(letters.length - 1, currentLetter + 1))}
            disabled={currentLetter === letters.length - 1}
            className="bg-blue-500 hover:bg-blue-600"
          >
            Lanjut
            <ArrowRight className="h-4 w-4 ml-2" />
          </Button>
        </div>
      </div>
    </div>
  )

  const ARScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-gray-400 to-gray-500 p-6">
      <div className="max-w-md mx-auto">
        <div className="flex items-center mb-6">
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")} className="mr-4 text-white">
            <ArrowLeft className="h-6 w-6" />
          </Button>
          <h1 className="text-2xl font-bold text-white">AR Mode</h1>
        </div>

        <Card className="bg-white/90 mb-6">
          <CardContent className="p-6 text-center">
            <Camera className="h-16 w-16 mx-auto mb-4 text-gray-600" />
            <p className="text-gray-700 mb-4">Arahkan kamera ke kartu isyarat untuk melihat animasi 3D</p>
            <Button className="bg-blue-500 hover:bg-blue-600 text-white">
              <Play className="h-4 w-4 mr-2" />
              Mulai AR
            </Button>
          </CardContent>
        </Card>

        <Card className="bg-amber-100">
          <CardContent className="p-6">
            <div className="flex items-center space-x-4">
              <div className="w-20 h-20 bg-yellow-400 rounded-lg flex items-center justify-center">
                <div className="text-2xl">👨</div>
              </div>
              <div className="flex-1">
                <h3 className="font-semibold text-gray-800 mb-1">Isyarat</h3>
                <p className="text-sm text-gray-600">Karakter 3D akan menunjukkan gerakan isyarat</p>
              </div>
            </div>
            <div className="flex justify-center space-x-4 mt-4">
              <Button variant="outline" size="sm">
                <RotateCcw className="h-4 w-4 mr-2" />
                Ulangi
              </Button>
              <Button variant="outline" size="sm">
                <Volume2 className="h-4 w-4 mr-2" />
                Suara
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )

  const QuizScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-green-200 to-green-300 p-6">
      <div className="max-w-md mx-auto">
        <div className="flex items-center mb-6">
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")} className="mr-4">
            <ArrowLeft className="h-6 w-6" />
          </Button>
          <h1 className="text-2xl font-bold text-gray-800">Kuis Isyarat</h1>
        </div>

        <Card className="bg-white mb-6">
          <CardContent className="p-6 text-center">
            <h2 className="text-xl font-semibold mb-4">Pertanyaan 1 dari 10</h2>
            <div className="text-6xl mb-4">✋</div>
            <p className="text-gray-700 mb-6">Huruf apakah yang ditunjukkan oleh isyarat ini?</p>

            <div className="grid grid-cols-2 gap-3">
              {["A", "B", "C", "D"].map((option) => (
                <Button
                  key={option}
                  variant="outline"
                  className="h-12 text-lg font-semibold hover:bg-green-100 bg-transparent"
                >
                  {option}
                </Button>
              ))}
            </div>
          </CardContent>
        </Card>

        <div className="text-center">
          <p className="text-gray-700 mb-2">Skor: 0 / 10</p>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div className="bg-green-500 h-2 rounded-full w-1/10"></div>
          </div>
        </div>
      </div>
    </div>
  )

  const AboutScreen = () => (
    <div className="min-h-screen bg-gradient-to-br from-purple-200 to-purple-300 p-6">
      <div className="max-w-md mx-auto">
        <div className="flex items-center mb-6">
          <Button variant="ghost" size="icon" onClick={() => setCurrentScreen("home")} className="mr-4">
            <ArrowLeft className="h-6 w-6" />
          </Button>
          <h1 className="text-2xl font-bold text-gray-800">Tentang Aplikasi</h1>
        </div>

        <Card className="bg-white">
          <CardContent className="p-6">
            <div className="text-center mb-6">
              <div className="text-6xl mb-4">🤟</div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">Isyarat Pintar</h2>
              <p className="text-gray-600">Versi 1.0.0</p>
            </div>

            <div className="space-y-4">
              <div>
                <h3 className="font-semibold text-gray-800 mb-2">Tentang SIBI</h3>
                <p className="text-sm text-gray-600">
                  Sistem Isyarat Bahasa Indonesia (SIBI) adalah sistem komunikasi visual yang digunakan oleh komunitas
                  tunarungu di Indonesia.
                </p>
              </div>

              <div>
                <h3 className="font-semibold text-gray-800 mb-2">Fitur Aplikasi</h3>
                <ul className="text-sm text-gray-600 space-y-1">
                  <li>• Pembelajaran huruf dengan isyarat SIBI</li>
                  <li>• Mode AR untuk visualisasi 3D</li>
                  <li>• Kuis interaktif</li>
                  <li>• Audio untuk setiap huruf</li>
                </ul>
              </div>

              <div>
                <h3 className="font-semibold text-gray-800 mb-2">Pengembang</h3>
                <p className="text-sm text-gray-600">
                  Dikembangkan untuk membantu pembelajaran bahasa isyarat bagi komunitas tunarungu Indonesia.
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )

  const screens = {
    splash: <SplashScreen />,
    home: <HomeScreen />,
    learn: <LearnScreen />,
    ar: <ARScreen />,
    quiz: <QuizScreen />,
    about: <AboutScreen />,
  }

  return <div className="max-w-md mx-auto bg-white min-h-screen">{screens[currentScreen]}</div>
}
