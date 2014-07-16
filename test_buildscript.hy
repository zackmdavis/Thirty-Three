(import unittest)
(import buildscript)


(defclass TestBuildscript [unittest.TestCase]
  [[test_can_diffract
    (fn [self]
      (self.assertEqual (buildscript.diffract "7f02ff")
                        [127 2 255]))]

   [test_can_undiffract
    (fn [self]
      (self.assertEqual (buildscript.undiffract [127 2 255])
                        "7f02ff"))]

   [test_can_average_diffracted
    (fn [self]
      (self.assertEqual (buildscript.average_diffracted [0 0 0] [254 254 254])
                        [127 127 127]))]

   [test_color_stop_interpolate
    (fn [self]
      (self.assertEqual (buildscript.color_stop_interpolate
                         {1 "000000" 3 "fefefe"} 2)
                        "7f7f7f"))]])
