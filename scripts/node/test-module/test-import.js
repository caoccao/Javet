import('./module1.js').then(obj => {
  console.log('resolved');
  const test1 = obj.test1;
  console.log(obj);
  console.log(test1());
}, obj => {
  console.log('rejected');
});
import('./module2.js').then(obj => {
  console.log('resolved');
  const test2 = obj.test2;
  console.log(obj);
  console.log(test2());
}, obj => {
  console.log('rejected');
});
